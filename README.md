# agoraRecord

docker 镜像构建完成后会下载agora依赖并编译

docker容器启动命令：docker run -it -p 8080:8080 -v {本地springboot jar包地址}:/usr/local/webapps/ {dockerImageId} bash

