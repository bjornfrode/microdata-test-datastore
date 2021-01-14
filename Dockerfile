FROM openjdk:15-jdk

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp
ADD build/libs/test-datastore-*.jar app.jar
RUN sh -c 'touch /app.jar' && \
    date +%Z
ENTRYPOINT ["java","--enable-preview","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
