package io.vavr.spring.convert;

import io.vavr.control.Option;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Collections;
import java.util.Set;

public class StringToOptionConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public StringToOptionConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return (targetType.getElementTypeDescriptor() == null ||
                this.conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor()));
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Option.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return Option.none();
        }

        String string = (String) source;
        if (string.isEmpty()) {
            return Option.none();
        }


        String value = string.trim();
        Option<SingleGenericTypeDescriptor> elementType =
                SingleGenericTypeDescriptor.resolve(targetType);

        if (elementType.isDefined()) {
            return elementType.map(e -> this.conversionService.convert(value, sourceType, e));
        }
        return Option.of(value);
    }
}
