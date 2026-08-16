package dev.jsbackend.graaljs;

import dev.jsbackend.api.GuestValue;
import org.graalvm.polyglot.Value;

final class GraalGuestValue implements GuestValue {

    private final GraalJsContext context;
    private final Value value;

    GraalGuestValue(GraalJsContext context, Value value) {
        this.context = context;
        this.value = value;
    }

    Value value() {
        return value;
    }

    @Override
    public boolean canExecute() {
        return value.canExecute();
    }

    @Override
    public Object execute(Object... args) {
        return context.convert(value.execute(args));
    }

    @Override
    public boolean isBoolean() {
        return value.isBoolean();
    }

    @Override
    public boolean asBoolean() {
        return value.asBoolean();
    }

    @Override
    public Object as(Class<?> type) {
        return value.as(type);
    }
}
