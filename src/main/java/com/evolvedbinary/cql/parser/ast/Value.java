package com.evolvedbinary.cql.parser.ast;

public class Value implements Expr {

    @Override
    public boolean equals(final Object obj) {
        if(obj != null && obj instanceof Value) {
            final Value other = (Value)obj;
            return true; //TODO(AR) implement
        }

        return false;
    }
}
