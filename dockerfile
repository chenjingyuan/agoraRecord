FROM java:8-jdk

WORKDIR /usr/local/

RUN mkdir webapps \
    && apt-get update && apt-get -qq install -y g++ make lsof 
RUN wget http://download.agora.io/sdk/release/Agora_Recording_SDK_for_Linux_v2_1_1_FULL.tar.gz \
    && tar zxvf Agora_Recording_SDK_for_Linux_v2_1_1_FULL.tar.gz \
    && cd /usr/local/Agora_Recording_SDK_for_Linux_FULL/samples/java \
    && /bin/bash -c "source ./build.sh pre_set /usr/lib/jvm/java-8-openjdk-amd64/include && ./build.sh build" \
    && mkdir lib

VOLUME /usr/local/webapps
EXPOSE 8080
