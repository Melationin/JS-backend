package dev.jsbackend.api;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration passed to {@link JsBackend#createContext}.
 */
public record JsContextConfig(
        Path workingDirectory,
        Map<String, String> options,
        Map<String, Object> globals,
        boolean allowAllAccess,
        boolean allowExperimentalOptions,
        boolean allowNativeAccess
) {

    public JsContextConfig {
        workingDirectory = workingDirectory == null
                ? Path.of(".").toAbsolutePath()
                : workingDirectory.toAbsolutePath();
        options = options == null ? Map.of() : Map.copyOf(options);
        globals = globals == null ? Map.of() : Map.copyOf(globals);
    }

    public static JsContextConfig of(Path workingDirectory, Map<String, String> options) {
        return new JsContextConfig(workingDirectory, options, Map.of(), true, true, true);
    }

    public JsContextConfig withGlobals(Map<String, Object> additionalGlobals) {
        Map<String, Object> merged = new LinkedHashMap<>(globals);
        if (additionalGlobals != null) {
            merged.putAll(additionalGlobals);
        }
        return new JsContextConfig(
                workingDirectory,
                options,
                merged,
                allowAllAccess,
                allowExperimentalOptions,
                allowNativeAccess
        );
    }
}
