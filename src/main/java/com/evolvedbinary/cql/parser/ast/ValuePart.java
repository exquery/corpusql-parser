package com.evolvedbinary.cql.parser.ast;

import org.jetbrains.annotations.Nullable;

public class ValuePart implements Expr {
    @Nullable private final String string;
    @Nullable private final Value value;

    public ValuePart(final String string) {
        this.string = string;
        this.value = null;
    }

    public ValuePart(final Value value) {
        this.string = null;
        this.value = value;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj != null && obj instanceof ValuePart) {
            final ValuePart other = (ValuePart)obj;
            if(this.string != null) {
                return this.string.equals(other.string);
            } else {
                return this.value.equals(other.value);
            }
        }

        return false;
    }
}
