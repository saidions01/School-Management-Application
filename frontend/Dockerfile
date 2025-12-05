# Build stage
FROM node:21-alpine AS builder

WORKDIR /app

COPY frontend/school-management-ui/package*.json ./

RUN npm ci

COPY frontend/school-management-ui .

RUN npm run build

# Runtime stage
FROM node:21-alpine

WORKDIR /app

RUN npm install -g http-server

COPY --from=builder /app/dist/school-management-ui/browser /app/dist

EXPOSE 4200

CMD ["http-server", "dist", "-p", "4200", "--proxy", "http://localhost:8080/?"]
