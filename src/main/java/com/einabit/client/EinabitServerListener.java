package com.einabit.client;

/**
 * Einabit Server Listener interface.
 * <p>
 * You need to implement this interface in your custom listener in order to manipulate
 * received values from Einabit services.
 */
public interface EinabitServerListener {

    /**
     * On subscribe method which will be executed everytime the client receives a value.
     *
     * @param variable variable to subscribe for
     */
    void onSubscribe(String variable);

}
