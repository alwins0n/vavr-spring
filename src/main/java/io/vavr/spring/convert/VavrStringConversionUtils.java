package io.vavr.spring.convert;

import io.vavr.control.Option;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.util.stream.Stream;

class VavrStringConversionUtils {

    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    static boolean isGenericTypeConvertable(TypeDescriptor targetType, ConversionService conversionService) {
        Option<SingleGenericTypeDescriptor> elementDesc =
                SingleGenericTypeDescriptor.resolve(targetType);

        return elementDesc
                .map(e -> conversionService.canConvert(STRING_TYPE_DESCRIPTOR, e))
                .getOrElse(true);
    }

    static Stream<?> toElementStream(Stream<String> source, TypeDescriptor targetType,
            ConversionService conversionService) {
        Option<SingleGenericTypeDescriptor> nestedElementDescriptor =
                SingleGenericTypeDescriptor.resolve(targetType);

        return nestedElementDescriptor.isDefined() ?
                source.map(f -> conversionService.convert(f.trim(), STRING_TYPE_DESCRIPTOR, nestedElementDescriptor.get())) :
                source;
    }

}
