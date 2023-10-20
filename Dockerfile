FROM gradle:8.3.0-jdk11-alpine

WORKDIR /tests
COPY . /tests
RUN gradle clean testClasses --no-daemon

CMD gradle test -DTAG=$TAG