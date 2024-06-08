# Stage 0, "build-stage", based on Node.js, to build and compile the frontend
# FROM node:13-alpine as build-stage
FROM trion/ng-cli:9.1.0 as build-stage
WORKDIR /app
# RUN npm install -g @angular/cli
COPY ./covid/package*.json ./
# RUN npm install --no-optional --no-shrinkwrap --no-package-lock
RUN npm install
COPY ./covid ./

RUN ng build --prod
# RUN npm run build --prod

# Stage 1, based on Nginx, to have only the compiled app, ready for production with Nginx
FROM nginx:1.17.1-alpine
WORKDIR /usr/share/nginx/html
COPY ./config/nginx.conf /etc/nginx/
COPY --from=build-stage /app/dist/covid ./

# When the container starts, replace the env.js with values from environment variables
CMD ["/bin/sh",  "-c",  "envsubst < /usr/share/nginx/html/assets/env.template.js > /usr/share/nginx/html/assets/env.js && exec nginx -g 'daemon off;'"]