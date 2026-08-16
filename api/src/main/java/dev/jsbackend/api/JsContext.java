package dev.jsbackend.api;

import java.nio.file.Path;

/**
 * A JavaScript execution context.
 * <p>
 * Implementations must be safe for the following usage pattern used by hosts:
 * a context is normally owned by one script execution thread. When the script
 * sleeps/waits (for example {@code waitForEvent}), the host calls
 * {@link #leave()}, lets other threads invoke callbacks on the same context,
 * then calls {@link #enter()} before resuming the script.
 */
public interface JsContext extends AutoCloseable {

    /**
     * Evaluates JavaScript source and returns the completion value as an opaque
     * handle. Executable results are returned as {@link GuestValue}.
     */
    Object eval(String code, String fileName);

    /**
     * Evaluates a script file.
     */
    Object evalFile(Path file);

    /**
     * Invokes a previously obtained function handle.
     *
     * @param function handle returned by {@link #eval} / {@link #evalFile} or a
     *                 {@link GuestValue}
     * @param thisArg  receiver, or {@code null}
     * @param args     arguments passed to the function
     * @return the function result as an opaque handle / Java value
     */
    Object execute(Object function, Object thisArg, Object... args);

    /**
     * Installs or replaces a script-visible global binding.
     */
    void setGlobal(String name, Object value);

    /**
     * Creates an empty guest object, used as per-script state.
     */
    Object createObject();

    /**
     * @return true when {@code value} is a callable guest function.
     */
    boolean isFunction(Object value);

    /**
     * Converts a returned script value to a boolean. Used for tick functions.
     */
    boolean isTrue(Object value);

    /**
     * Enters the context on the current thread. Only needed when a context may
     * be shared between threads (e.g. while a script is sleeping).
     */
    void enter();

    /**
     * Leaves the context on the current thread. See {@link #enter()}.
     */
    void leave();

    /**
     * Closes the context. If {@code cancelIfExecuting} is {@code true}, any
     * currently running script must be interrupted.
     */
    void close(boolean cancelIfExecuting);

    @Override
    default void close() {
        close(false);
    }
}
