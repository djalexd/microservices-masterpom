FROM java:8

RUN mkdir /spring-apps

ADD sensor-config.jar /spring-apps
ADD run-app.sh /spring-apps

WORKDIR /spring-apps

ENTRYPOINT ["./run-app.sh"]
#CMD ["java", "-jar", "sensor-config.jar"]