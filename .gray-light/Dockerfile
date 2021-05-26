FROM openjdk:11

ARG app=gray-light-gateway
ARG target=target
ARG version=1.0-SNAPSHOT
ARG package=${app}-${version}.jar

ENV GRAY_LIGHT_PACKAGE=/usr/app/${app}-${version}.jar

COPY $target/$package /usr/app/

ENTRYPOINT java -jar $(echo ${GRAY_LIGHT_PACKAGE})