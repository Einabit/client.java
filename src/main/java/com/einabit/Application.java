package com.einabit;

import com.einabit.client.EinabitClient;
import com.einabit.client.EinabitServerListener;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class Application {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    private static final String TEMP_1 = "temp1";
    private static final String CRG_1 = "crg1";

    public static void main(String[] args) {

        // Instantiate new Einabit client using provided builder, by default the port is 1337
        final EinabitClient client = EinabitClient.builder()
                .host(System.getenv("EINABIT_HOST"))
                .key(System.getenv("PASSPHRASE"))
                .build();

        // Instantiate our custom listener which implements EinabitServerListener
        final MyCustomListener listener = new MyCustomListener();

        // Manually create your own thread and run it asynchronously
        final Thread myThread = new MyThread(client, listener);
        myThread.start();

        // Run your custom runnable asynchronously without blocking main thread
        final Thread myRunnableThread = new Thread(new MyRunnable(client, listener), "my-runnable-thread-0");

        // We can use a completable future to run it on a specific thread pool passing an executor or
        // just run it as myRunnableThread.start() to have more control over it.
        CompletableFuture.runAsync(myRunnableThread::start);


        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(500);
                System.out.println(Thread.currentThread().getName() + ": Current value temp1: " + client.value(TEMP_1));
            } catch (InterruptedException e) {
                LOGGER.severe("Error: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        try {
            System.out.println("Sleeping for 5s");
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            LOGGER.severe("Error: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Stopping all subscriptions");
            myThread.interrupt();
            myRunnableThread.interrupt();
        }
    }

    // Our implementation of EinabitServerListener for subscribing to tap operation
    private static class MyCustomListener implements EinabitServerListener {

        @Override
        public void onSubscribe(String value) {
            System.out.println(Thread.currentThread().getName() + ": Value received from subscription: " + value);
        }

    }

    private record MyRunnable(EinabitClient client, EinabitServerListener listener) implements Runnable {

        @Override
        public void run() {
            client.tap(TEMP_1, listener);
        }

    }

    private static class MyThread extends Thread {

        private final EinabitClient client;
        private final EinabitServerListener listener;

        public MyThread(EinabitClient client, EinabitServerListener listener) {
            super("my-thread-1");
            this.client = client;
            this.listener = listener;
        }

        @Override
        public void run() {
            client.tap(CRG_1, listener);
        }
    }

}
