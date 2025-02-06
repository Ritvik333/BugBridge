FROM node:18-alpine

WORKDIR /app

COPY package.json .

RUN npm install

COPY . .

ARG BACKEND_BASE_URL
ENV BACKEND_BASE_URL=$BACKEND_BASE_URL
RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]