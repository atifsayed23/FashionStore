FROM tomcat:10.1-jdk21

# Copy the built WAR file to the Tomcat webapps directory
COPY target/FashionStore.war /usr/local/tomcat/webapps/FashionStore.war

# Note: Port 8080 exposure and the startup command (CMD ["catalina.sh", "run"]) 
# are already handled automatically by the base tomcat image.