package com.evolvedbinary.cql.parser.ast;

import org.jetbrains.annotations.Nullable;

/**
 * Represents an Attribute Value Query
 */
public class AttributeValue implements Expr {
    @Nullable private final String attribute;
    private final ComparisonType comparisonType;
    private final ValuePart value;

    public enum ComparisonType {
        EQUAL,
        NOT_EQUAL
    }

    /**
     * @param attribute The attribute name, or null to use the default attribute
     * @param comparisonType The comparison between the attribute and its value
     * @param value The value to compare the attribute against
     */
    public AttributeValue(@Nullable final String attribute, final ComparisonType comparisonType, final ValuePart value) {
        this.attribute = attribute;
        this.comparisonType = comparisonType;
        this.value = value;
    }

    /**
     * @return true if there is no attribute name, and so the default attribute
     *   should be used
     */
    public boolean useDefaultAttribute() {
        return attribute == null;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj != null && obj instanceof AttributeValue) {
            final AttributeValue other = (AttributeValue)obj;
            return this.attribute.equals(other.attribute) &&
                    this.comparisonType == other.comparisonType &&
                    this.value.equals(other.value);
        }

        return false;
    }
}
