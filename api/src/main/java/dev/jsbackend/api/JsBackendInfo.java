package dev.jsbackend.api;

/**
 * Static metadata about a JS backend.
 */
public record JsBackendInfo(
        String name,
        String engineVersion,
        String language,
        String esVersion,
        boolean supportsCommonJs,
        boolean supportsPromises,
        String threadingModel
) {
}
