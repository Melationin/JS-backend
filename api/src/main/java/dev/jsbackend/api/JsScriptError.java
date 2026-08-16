package dev.jsbackend.api;

/**
 * Uniform script error with user-facing location information.
 */
public class JsScriptError extends RuntimeException {

    private final String scriptName;
    private final int line;
    private final int column;

    public JsScriptError(String message, String scriptName, int line, int column, Throwable cause) {
        super(message, cause);
        this.scriptName = scriptName;
        this.line = line;
        this.column = column;
    }

    public String getScriptName() {
        return scriptName;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.getMessage() == null ? getClass().getSimpleName() : super.getMessage());
        if (scriptName != null) {
            sb.append(" at ").append(scriptName);
            if (line > 0) {
                sb.append(':').append(line);
                if (column > 0) {
                    sb.append(':').append(column);
                }
            }
        }
        return sb.toString();
    }
}
