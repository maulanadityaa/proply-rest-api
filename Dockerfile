FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/proply-backend-0.0.1-SNAPSHOT.jar proply-backend.jar
ENTRYPOINT ["java","-jar","/proply-backend.jar"]