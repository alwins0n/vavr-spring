package io.vavr.spring.convert;

import io.vavr.collection.Seq;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Arrays;
import java.util.Collections;

import static io.vavr.spring.convert.VavrStringConversionUtils.isGenericTypeConvertable;

public class StringArrayToVavrSeqConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public StringArrayToVavrSeqConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return isGenericTypeConvertable(targetType, conversionService);
    }

    @Override
    public java.util.Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String[].class, Seq.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        return VavrStringToSeqConversionUtils.convert(Arrays.stream((String[]) source), targetType, conversionService);
    }

}
