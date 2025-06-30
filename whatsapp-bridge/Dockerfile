# Usa una imagen base oficial de Node.js
FROM node:20-slim

# Establece el directorio de trabajo
WORKDIR /usr/src/app

# Copia los archivos de definición de paquetes
COPY package*.json ./

# Instala las dependencias del proyecto e instala las dependencias de puppeteer
RUN apt-get update && apt-get install -yq libgconf-2-4 libatk1.0-0 libatk-bridge2.0-0 libcups2 libgtk-3-0 libgbm-dev libnss3 libxss1 libasound2 && npm install

# Copia el resto del código de la aplicación
COPY . .

# Expone el puerto en el que se ejecuta el bridge
EXPOSE 21465

# Comando para iniciar la aplicación (ajusta 'index.js' si tu archivo principal es otro)
CMD [ "node", "server.js" ]