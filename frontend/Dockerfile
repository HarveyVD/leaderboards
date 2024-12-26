FROM nginx:alpine

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy static files
COPY . /usr/share/nginx/html

# Replace API_URL in index.html with environment variable at runtime
CMD ["/bin/sh", "-c", "envsubst '${API_URL}' < /usr/share/nginx/html/index.html > /usr/share/nginx/html/index.html.tmp && mv /usr/share/nginx/html/index.html.tmp /usr/share/nginx/html/index.html && nginx -g 'daemon off;'"] 