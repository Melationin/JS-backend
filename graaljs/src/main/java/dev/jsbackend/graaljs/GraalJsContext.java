package dev.jsbackend.graaljs;

import dev.jsbackend.api.GuestValue;
import dev.jsbackend.api.JsContext;
import dev.jsbackend.api.JsContextConfig;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

final class GraalJsContext implements JsContext {

    private final Context context;

    GraalJsContext(JsContextConfig config) {
        Context.Builder builder = Context.newBuilder("js")
                .engine(GraalJsBackend.SHARED_ENGINE)
                .allowAllAccess(config.allowAllAccess())
                .allowExperimentalOptions(config.allowExperimentalOptions())
                .allowNativeAccess(config.allowNativeAccess());

        Path cwd = config.workingDirectory();
        if (cwd != null) {
            builder.currentWorkingDirectory(cwd.toAbsolutePath());
        }

        for (Map.Entry<String, String> option : config.options().entrySet()) {
            builder.option(option.getKey(), option.getValue());
        }

        // Sensible defaults for Minecraft scripting.
        if (!config.options().containsKey("js.commonjs-require")) {
            builder.option("js.commonjs-require", "true");
        }
        if (cwd != null && !config.options().containsKey("js.commonjs-require-cwd")) {
            builder.option("js.commonjs-require-cwd", cwd.toAbsolutePath().toString());
        }

        this.context = builder.build();

        for (Map.Entry<String, Object> global : config.globals().entrySet()) {
            setGlobal(global.getKey(), global.getValue());
        }
    }

    @Override
    public Object eval(String code, String fileName) {
        enter();
        try {
            Source source = Source.newBuilder("js", code, fileName == null ? "<eval>" : fileName).build();
            return convert(context.eval(source));
        } catch (IOException e) {
            throw new IllegalStateException("Could not build JS source: " + fileName, e);
        } finally {
            leave();
        }
    }

    @Override
    public Object evalFile(Path file) {
        enter();
        try {
            Source source = Source.newBuilder("js", file.toFile()).build();
            return convert(context.eval(source));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read JS file: " + file, e);
        } finally {
            leave();
        }
    }

    @Override
    public Object execute(Object function, Object thisArg, Object... args) {
        enter();
        try {
            Value fn = (Value) toValue(function);
            Object[] convertedArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                convertedArgs[i] = toValue(args[i]);
            }
            return convert(fn.execute(convertedArgs));
        } finally {
            leave();
        }
    }

    @Override
    public void setGlobal(String name, Object value) {
        enter();
        try {
            context.getBindings("js").putMember(name, toValue(value));
        } finally {
            leave();
        }
    }

    @Override
    public Object createObject() {
        enter();
        try {
            return new GraalGuestValue(this, context.eval("js", "({})"));
        } finally {
            leave();
        }
    }

    @Override
    public boolean isFunction(Object value) {
        return value instanceof GuestValue guest && guest.canExecute();
    }

    @Override
    public boolean isTrue(Object value) {
        if (value instanceof GuestValue guest && guest.isBoolean()) {
            return guest.asBoolean();
        }
        return Boolean.TRUE.equals(value);
    }

    @Override
    public void enter() {
        context.enter();
    }

    @Override
    public void leave() {
        context.leave();
    }

    @Override
    public void close(boolean cancelIfExecuting) {
        context.close(cancelIfExecuting);
    }

    Object toValue(Object value) {
        if (value instanceof GraalGuestValue guest) {
            return guest.value();
        }
        if (value instanceof Value graalValue) {
            return graalValue;
        }
        // Graal accepts plain Java values directly.
        return value;
    }

    Object convert(Value value) {
        if (value == null) {
            return null;
        }
        if (value.isHostObject()) {
            return value.asHostObject();
        }
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (value.isString()) {
            return value.asString();
        }
        if (value.isNumber()) {
            return value.as(Object.class);
        }
        if (value.canExecute() || value.hasMembers()) {
            return new GraalGuestValue(this, value);
        }
        try {
            return value.as(Object.class);
        } catch (Throwable t) {
            return new GraalGuestValue(this, value);
        }
    }
}
