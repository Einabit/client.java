FROM openjdk:11

COPY . /usr/src/einabit

WORKDIR /usr/src/einabit

RUN javac -d build -cp . src/main/java/com/einabit/Application.java src/main/java/com/einabit/client/*.java

CMD ["java", "-cp", "./build", "com.einabit.Application"]
