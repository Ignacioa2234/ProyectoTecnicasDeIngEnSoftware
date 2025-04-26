# etapa 1: build con node
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build         # genera /app/dist

# etapa 2: nginx sirve estáticos
FROM nginx:alpine
# limpia cualquier contenido por defecto
RUN rm -rf /usr/share/nginx/html/*
# copia tu build
COPY --from=builder /app/dist /usr/share/nginx/html
# (opcional) copia un nginx.conf local si quieres manejo de rutas HTML5
# COPY nginx/frontend.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx","-g","daemon off;"]
