package io.vavr.spring.convert;

import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;
import io.vavr.control.Option;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class StringToVavrSetConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public StringToVavrSetConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Option<SingleGenericTypeDescriptor> elementDesc =
                SingleGenericTypeDescriptor.resolve(targetType);

        return elementDesc
                .map(e -> isConvertible(sourceType, targetType, e))
                .getOrElse(true);
    }

    private boolean isConvertible(TypeDescriptor sourceType, TypeDescriptor targetType, SingleGenericTypeDescriptor elementType) {
        boolean canConvert = this.conversionService.canConvert(sourceType, elementType);
        if (isElementComparableRequired(targetType, elementType)) {
            return canConvert && Comparable.class.isAssignableFrom(elementType.getType());
        }

        return canConvert;
    }

    private boolean isElementComparableRequired(TypeDescriptor targetType, TypeDescriptor elementType) {
        final boolean needsComparable = SortedSet.class.isAssignableFrom(targetType.getType());
        if (!needsComparable) {
            return true;
        } else {
            return Comparable.class.isAssignableFrom(elementType.getType());
        }
    }

    @Override
    public java.util.Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Set.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        Stream<String> stream = Arrays.stream(
                StringUtils.commaDelimitedListToStringArray((String) source));

        return VavrStringToSetConversionUtils.convert(stream, targetType, conversionService);
    }

}
