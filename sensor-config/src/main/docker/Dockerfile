FROM java:8

RUN mkdir /spring-apps

ADD sensor-config.jar /spring-apps

WORKDIR /spring-apps

# Concatenate all parameters that were received when running this
# container as runtime parameters to java process.
CMD ["java", "-jar", "sensor-config.jar"]