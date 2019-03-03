package io.vavr.spring.convert;

import io.vavr.control.Option;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

class VavrConversionUtils {

    static boolean isGenericTypeConvertable(TypeDescriptor sourceType, TypeDescriptor targetType, ConversionService conversionService) {
        Option<SingleGenericTypeDescriptor> elementDesc =
                SingleGenericTypeDescriptor.resolve(targetType);

        return elementDesc
                .map(e -> conversionService.canConvert(sourceType, e))
                .getOrElse(true);
    }

}
