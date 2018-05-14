FROM java:8-jdk

WORKDIR /usr/local/

RUN mkdir webapps \
    && apt-get update && apt-get install -y g++ make \
    && wget http://download.agora.io/sdk/release/Agora_Recording_SDK_for_Linux_v2_1_1_FULL.tar.gz \
    && tar zxvf Agora_Recording_SDK_for_Linux_v2_1_1_FULL.tar.gz

WORKDIR /usr/local/Agora_Recording_SDK_for_Linux_FULL/samples/java
RUN /bin/bash -c "source ./build.sh pre_set /usr/lib/jvm/java-8-openjdk-amd64/include && ./build.sh build" \
    && mkdir lib

VOLUME /usr/local/webapps
EXPOSE 8080
