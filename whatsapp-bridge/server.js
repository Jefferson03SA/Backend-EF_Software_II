const express = require('express');
const cors = require('cors');
const wppconnect = require('@wppconnect-team/wppconnect');

const app = express();
const port = 21465;

app.use(cors());
app.use(express.json());

let client = null;

// Función para formatear número
function formatPhoneNumber(phoneNumber) {
    let formattedNumber = phoneNumber;
    if (!formattedNumber.includes('@c.us')) {
        if (!formattedNumber.startsWith('51')) {
            formattedNumber = '51' + formattedNumber;
        }
        formattedNumber = formattedNumber + '@c.us';
    }
    return formattedNumber;
}

// Función para imprimir objetos de manera legible
function prettyLog(title, obj) {
    console.log('\n' + '='.repeat(50));
    console.log(title);
    console.log('-'.repeat(50));
    console.log(JSON.stringify(obj, null, 2));
    console.log('='.repeat(50) + '\n');
}

// Inicializar el cliente de WhatsApp
async function initWhatsApp() {
    try {
        client = await wppconnect.create({
            session: 'whatsapp-session',
            puppeteerOptions: {
                args: ['--no-sandbox']
            },
            catchQR: (base64Qr, asciiQR) => {
                console.log('\n🔄 NUEVO CÓDIGO QR GENERADO:');
                console.log(asciiQR);
            },
            statusFind: (statusSession, session) => {
                console.log('\n📱 ESTADO DE LA SESIÓN:', statusSession);
                console.log('📍 NOMBRE DE LA SESIÓN:', session);
            },
        });
        console.log('\n✅ Cliente de WhatsApp iniciado correctamente');
    } catch (error) {
        console.error('\n❌ Error al iniciar el cliente de WhatsApp:', error);
    }
}

// Endpoint para verificar si un número existe en WhatsApp
app.get('/api/check-number/:phoneNumber', async (req, res) => {
    try {
        if (!client) {
            return res.status(500).json({ error: 'Cliente de WhatsApp no inicializado' });
        }

        const phoneNumber = formatPhoneNumber(req.params.phoneNumber);
        console.log('\n🔍 Verificando número:', phoneNumber);

        // Usar el método específico para verificar números
        const numberExists = await client.checkNumberStatus(phoneNumber);
        prettyLog('Resultado de verificación de número', numberExists);

        // Si el número no existe o no es válido
        if (!numberExists || numberExists.status === 404 || !numberExists.canReceiveMessage) {
            console.log('❌ Número no existe o no puede recibir mensajes');
            return res.json({
                exists: false,
                phoneNumber: req.params.phoneNumber,
                error: 'Número no existe en WhatsApp o no puede recibir mensajes',
                timestamp: new Date().toISOString()
            });
        }

        // Intentar obtener información adicional solo si el número existe
        let profileInfo = {};
        try {
            console.log('📱 Obteniendo información adicional del perfil...');
            
            // Obtener foto de perfil
            const profilePic = await client.getProfilePicFromServer(phoneNumber);
            
            // Obtener estado usando el método específico para estados
            const statusInfo = await client.getStatus(phoneNumber);
            prettyLog('Estado obtenido', statusInfo);
            
            // Intentar obtener información del perfil completo
            const contactInfo = await client.getContact(phoneNumber);
            prettyLog('Información de contacto', contactInfo);

            profileInfo = {
                hasProfilePic: !!profilePic,
                status: statusInfo?.status || contactInfo?.status || 'No disponible',
                pushname: contactInfo?.pushname || '',
                isBusiness: contactInfo?.isBusiness || false,
                isEnterprise: contactInfo?.isEnterprise || false
            };
            
            prettyLog('Información del perfil procesada', profileInfo);
        } catch (error) {
            console.log('⚠️ Error al obtener información adicional:', error.message);
            profileInfo = {
                hasProfilePic: false,
                status: 'No disponible',
                pushname: '',
                isBusiness: false,
                isEnterprise: false
            };
        }
        
        const response = {
            exists: true,
            phoneNumber: phoneNumber,
            ...profileInfo,
            canReceiveMessage: numberExists.canReceiveMessage,
            numberInfo: numberExists,
            timestamp: new Date().toISOString()
        };

        prettyLog('Respuesta final', response);
        res.json(response);
    } catch (error) {
        console.error('❌ Error al verificar número:', error);
        res.status(500).json({ 
            error: error.message,
            details: error.stack
        });
    }
});

// Endpoint para enviar mensajes
app.post('/api/send-message', async (req, res) => {
    try {
        if (!client) {
            return res.status(500).json({ error: 'Cliente de WhatsApp no inicializado' });
        }

        const { phoneNumber, message } = req.body;
        const formattedNumber = formatPhoneNumber(phoneNumber);

        console.log('\n📤 Intentando enviar mensaje a:', formattedNumber);

        // Verificar primero si el número existe
        const numberExists = await client.checkNumberStatus(formattedNumber);
        prettyLog('Verificación del número antes de enviar', numberExists);

        if (!numberExists || numberExists.status === 404 || !numberExists.canReceiveMessage) {
            console.log('❌ No se puede enviar mensaje: número inválido o no puede recibir mensajes');
            return res.status(400).json({
                error: 'El número no existe en WhatsApp o no puede recibir mensajes',
                phoneNumber: formattedNumber
            });
        }

        console.log('📨 Enviando mensaje...');
        const result = await client.sendText(formattedNumber, message);
        prettyLog('Resultado del envío', result);
        
        res.json({ 
            success: true,
            to: formattedNumber,
            message: message,
            result: result
        });
    } catch (error) {
        console.error('❌ Error al enviar mensaje:', error);
        res.status(500).json({ 
            error: error.message,
            details: error.stack
        });
    }
});

// Endpoint para verificar el estado de la conexión
app.get('/api/status', (req, res) => {
    const status = {
        connected: client !== null,
        timestamp: new Date().toISOString()
    };
    prettyLog('Estado del servidor', status);
    res.json(status);
});

// Iniciar el servidor
app.listen(port, () => {
    console.log(`\n🚀 Servidor bridge de WhatsApp ejecutándose en http://localhost:${port}`);
    console.log('⏳ Iniciando cliente de WhatsApp...');
    initWhatsApp();
}); 