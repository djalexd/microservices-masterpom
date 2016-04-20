FROM java:8

RUN mkdir /spring-apps

ADD config-server.jar /spring-apps

WORKDIR /spring-apps

# Concatenate all parameters that were received when running this
# container as runtime parameters to java process.
CMD ["java", "-jar", "config-server.jar"]