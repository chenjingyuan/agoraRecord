# agoraRecord

docker 镜像构建完成后会下载agora依赖并编译

docker容器启动命令：docker run -it -p 80:8080 -v {本地springboot jar包地址}:/usr/local/webapps/ {dockerImageId} bash


本地开发项目后，将springboot的jar包放到上个命令配置的地址，进入容器后在/usr/local/webapps/目录下就和以找到这个jar包，直接运行就可以
