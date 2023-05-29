package com.einabit.client;

/**
 * Available operations in Einabit services
 */
public enum Operation {

    /**
     * Value operation.
     * <p>
     * Used to obtain the current value of a variable.
     */
    VALUE,

    /**
     * Fetch operation.
     * <p>
     * Used to obtain all the values of a variable between two dates.
     */
    FETCH,

    /**
     * Tap operation.
     * <p>
     * Operation which allows you to subscribe to a variable and continuously receive its values.
     */
    TAP

}
