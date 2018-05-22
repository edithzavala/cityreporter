FROM java:8
VOLUME /tmp
ADD /build/libs/cityreporter.jar cityreporter.jar
ENTRYPOINT ["java","-jar","cityreporter.jar"]
