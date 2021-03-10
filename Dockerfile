FROM adoptopenjdk:14

WORKDIR /setup
RUN apt-get update
RUN apt-get install unzip groff libsqlite3-dev -y
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && unzip awscliv2.zip && ./aws/install

WORKDIR /app
