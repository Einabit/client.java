### Descripción

Tenemos 2 imágenes, la primera es el sandbox que la vamos a descargar, crear un contenedor e inyectar la configuración para generar valores aleatorios en temp1.

Por otro lado vamos a generar un contenedor de java con el código de `Main.java` que deberá conectar un socket al contenedor `sandbox` para enviar el mensaje "value,temp1" que indica al servidor que debe devolver el último valor de `temp1`.

### Ejecutar el código

`docker compose up --build`

esperado:

```
java-test-sandbox-1   | Server started
java-test-java-sdk-1  | Connected to server sandbox/172.22.0.2:1337
java-test-sandbox-1   | new connection received with command value args: temp1
java-test-java-sdk-1  | Response from server: temp1,1,1677580445522
```
