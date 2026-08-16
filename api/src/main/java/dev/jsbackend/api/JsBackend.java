package dev.jsbackend.api;

import java.nio.file.Path;

/**
 * A pluggable JavaScript backend shared by JsMacros262 and BotMacro.
 * <p>
 * Implementations are discovered through {@link java.util.ServiceLoader} and/or
 * the Fabric {@code "jsbackend"} entrypoint. Multiple backend mods may be
 * installed; hosts choose one by id or by priority in {@code auto} mode.
 */
public interface JsBackend {

    /**
     * @return stable unique id, e.g. {@code "graaljs"}.
     */
    String id();

    /**
     * @return human-readable display name.
     */
    String displayName();

    /**
     * @return backend implementation version.
     */
    String version();

    /**
     * Higher priority wins in {@code auto} mode when several backends match the
     * same file.
     */
    int priority();

    /**
     * @return whether this backend can execute the given script file.
     */
    boolean supportsFile(Path file);

    /**
     * @return default file extension, e.g. {@code "js"}.
     */
    String defaultFileExtension();

    /**
     * @return static backend metadata.
     */
    JsBackendInfo info();

    /**
     * Creates a new JS execution context.
     */
    JsContext createContext(JsContextConfig config);

    /**
     * Converts a backend-specific exception into a uniform {@link JsScriptError}.
     */
    JsScriptError wrapException(Throwable throwable);

    /**
     * @return whether {@code value} is a guest-language object owned by this
     * backend (usually a {@link GuestValue}).
     */
    boolean isGuestObject(Object value);
}
