package dev.jsbackend.api;

/**
 * Opaque handle to a guest-language value.
 * <p>
 * Hosts use this to hold JavaScript functions/objects without depending on a
 * concrete backend type. All invocation goes through {@link JsContext#execute}.
 */
public interface GuestValue {

    /**
     * @return whether this guest value is callable.
     */
    boolean canExecute();

    /**
     * Invokes this guest value as a function.
     *
     * @param args arguments passed to the function
     * @return the result as an opaque handle / Java value
     */
    Object execute(Object... args);

    /**
     * @return whether this guest value is a boolean.
     */
    boolean isBoolean();

    /**
     * @return the boolean value; only valid when {@link #isBoolean()} is true.
     */
    boolean asBoolean();

    /**
     * Converts this guest value to the requested Java type when supported.
     */
    Object as(Class<?> type);
}
