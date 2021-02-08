FROM ubuntu:latest
ARG DEBIAN_FRONTEND=noninteractive
MAINTAINER bhanu@bhanu.com
RUN apt-get update 
RUN apt-get install apache2 -y

EXPOSE 80

CMD /usr/sbin/apache2ctl -D FOREGROUND
