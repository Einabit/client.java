package com.einabit;

import com.einabit.client.EinabitClient;
import com.einabit.client.EinabitServerListener;

import java.util.concurrent.CompletableFuture;

public class Application {

    private static final String TEMP_1 = "temp1";
    private static final String CRG_1 = "crg1";

    public static void main(String[] args) {

        // Instantiate new Einabit client using provided builder, by default the port is 1337
        final EinabitClient client = EinabitClient.builder()
                .host(System.getenv("EINABIT_HOST"))
                .build();

        // Async example with completable future
        final CompletableFuture<String> crgFuture = CompletableFuture
                .supplyAsync(() -> client.value(CRG_1));

        // Get current value of temp1 synchronously
        final String temp1 = client.value(TEMP_1);

        // Once the future is complete print values of temp1 and crg1
        crgFuture.thenAccept(crg1 -> {
            System.out.println("Value from async function: " + crg1);
            System.out.println("Value from synchronous execution: " + temp1);
        });

        // Instantiate our custom listener which implements EinabitServerListener
        final MyCustomListener listener = new MyCustomListener();

        // Subscribe to receive temp1 values, we could run it in a different thread to avoid blocking main thread
        client.tap(TEMP_1, listener);
    }

    // Our implementation of EinabitServerListener for subscribing to tap operation
    private static class MyCustomListener implements EinabitServerListener {

        @Override
        public void onSubscribe(String value) {
            System.out.println(Thread.currentThread().getName() + ": Value received from subscription: " + value);
        }

    }

}
