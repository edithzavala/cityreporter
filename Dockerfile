FROM java:8
VOLUME /tmp
ADD /build/libs/cityreporter.jar cityreporter.jar
EXPOSE 8089
ENTRYPOINT ["java","-jar","cityreporter.jar"]
