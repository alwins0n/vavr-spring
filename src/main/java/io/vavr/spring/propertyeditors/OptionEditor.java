package io.vavr.spring.propertyeditors;

import io.vavr.NotImplementedError;
import io.vavr.control.Option;

import java.beans.PropertyEditorSupport;

class OptionEditor extends PropertyEditorSupport {

    /**
     * Convert the given text value to a Collection with a single element.
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(text);
    }

    /**
     * Convert the given value to a Collection of the target type.
     */
    @Override
    public void setValue(Object value) {
        super.setValue(value);
    }

    @Override
    public Object getValue() {
        final Object value = super.getValue();
        final boolean emptyString = value instanceof String && ((String) value).isEmpty();
        if (value == null || emptyString) {
            return Option.none();
        } else {
            // TODO
            throw new NotImplementedError();
        }
    }
}
