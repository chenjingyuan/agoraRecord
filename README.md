# agoraRecord

docker 镜像构建完成后会下载agora依赖并编译

docker容器启动命令：docker run -it -p 8080:8080 -v {本地springboot jar包地址}:/usr/local/webapps/ {dockerImageId} bash

如果进入容器后没有LD_LIBRARY_PATH 环境变量的话，还需要在容器中的/usr/local/Agora_Recording_SDK_for_Linux_FULL/samples/java目录下执行
source ./build.sh pre_set /usr/lib/jvm/java-8-openjdk-amd64/include && ./build.sh build

本地开发项目后，将springboot的jar包放到上个命令配置的地址，进入容器后在/usr/local/webapps/目录下就和以找到这个jar包，直接运行就可以
