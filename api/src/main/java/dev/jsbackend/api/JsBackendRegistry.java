package dev.jsbackend.api;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Small discovery helper for {@link JsBackend} implementations.
 * <p>
 * Fabric hosts should additionally query the {@code "jsbackend"} Fabric
 * entrypoint; this class intentionally stays Fabric-free.
 */
public final class JsBackendRegistry {

    private JsBackendRegistry() {
    }

    public static List<JsBackend> load(ClassLoader classLoader) {
        List<JsBackend> result = new ArrayList<>();
        for (JsBackend backend : ServiceLoader.load(JsBackend.class, classLoader)) {
            result.add(backend);
        }
        return result;
    }

    public static List<JsBackend> load() {
        return load(Thread.currentThread().getContextClassLoader());
    }
}
