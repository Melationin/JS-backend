package dev.jsbackend.graaljs;

import dev.jsbackend.api.GuestValue;
import dev.jsbackend.api.JsBackend;
import dev.jsbackend.api.JsBackendInfo;
import dev.jsbackend.api.JsContext;
import dev.jsbackend.api.JsContextConfig;
import dev.jsbackend.api.JsScriptError;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import java.nio.file.Path;

/**
 * GraalJS implementation of {@link JsBackend}.
 */
public final class GraalJsBackend implements JsBackend {

    static final Engine SHARED_ENGINE = Engine.newBuilder()
            .option("engine.WarnInterpreterOnly", "false")
            .build();

    @Override
    public String id() {
        return "graaljs";
    }

    @Override
    public String displayName() {
        return "GraalJS";
    }

    @Override
    public String version() {
        return SHARED_ENGINE.getVersion();
    }

    @Override
    public int priority() {
        return 1000;
    }

    @Override
    public boolean supportsFile(Path file) {
        String name = file.getFileName().toString();
        return name.endsWith(".js") || name.endsWith(".mjs");
    }

    @Override
    public String defaultFileExtension() {
        return "js";
    }

    @Override
    public JsBackendInfo info() {
        return new JsBackendInfo(
                "graaljs",
                SHARED_ENGINE.getVersion(),
                "js",
                "ECMAScript 2024+",
                true,
                true,
                "single-thread"
        );
    }

    @Override
    public JsContext createContext(JsContextConfig config) {
        return new GraalJsContext(config);
    }

    @Override
    public JsScriptError wrapException(Throwable throwable) {
        if (throwable instanceof PolyglotException pe) {
            String message = pe.getMessage();
            Throwable cause = throwable;
            if (pe.isHostException()) {
                cause = pe.asHostException();
                message = cause.getClass().getSimpleName() + ": " + cause.getMessage();
            }
            Source source = pe.getSourceLocation() != null ? pe.getSourceLocation().getSource() : null;
            String name = source != null ? source.getName() : null;
            int line = pe.getSourceLocation() != null ? pe.getSourceLocation().getStartLine() : 0;
            int column = pe.getSourceLocation() != null ? pe.getSourceLocation().getStartColumn() : 0;
            return new JsScriptError(message, name, line, column, cause);
        }
        return new JsScriptError(
                throwable.getClass().getSimpleName() + ": " + throwable.getMessage(),
                null, 0, 0, throwable
        );
    }

    @Override
    public boolean isGuestObject(Object value) {
        return value instanceof GuestValue;
    }
}
