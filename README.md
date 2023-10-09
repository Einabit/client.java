# Documentation

This repository contains the SDK or java library which allows you to query Einabit services in an easy way. Everything 
out of the box, just provide the host to the Client builder and start querying our services. Currently, we support three
operations:

- __value__: 
  ```java
    /*
     * Get current value of a variable.
     *
     * @param variable variable
     * @return variable value
     */
    public String value(final String variable);
  ```
- __fetch__:
  ```java
    /*
     * Fetch values of a variable.
     *
     * @param variable variable
     * @param from     from timestamp
     * @param to       to timestamp
     * @return values delimited by commas
     */
    public String fetch(final String variable, final long from, final long to);
  ```
- __tap__:
  ```java
    /*
     * Subscribe to a variable.
     *
     * @param variable variable
     * @param callback callback which will be executed everytime it receives a value.
     */
    public void tap(final String variable, final EinabitServerListener callback);
  ```
- __last__:
  ```java
    /*
     * Fetch last n values of a variable.
     *
     * @param variable variable
     * @param amount   amount of values to fetch
     * @return values delimited by commas
     */
    public String last(final String variable, final Integer amount);
  ```
# Usage example

1. Create the client:
    ```java
    final EinabitClient einabitClient = EinabitClient.builder()
        .host("localhost")
        .build();
    ```         

2. Query a variable using the client:
    ```java
    final String tempValue = einabitClient.value("temp");
    ```

# Installation

This library is available in Maven central repository.

# Testing

In case you´d rather to test the client, or even create your own, you can pull and run our docker image
which will provide you with mocked data to play with and make your development easier. 

You can find the instructions for setting it up here: https://github.com/Einabit/sandbox.
