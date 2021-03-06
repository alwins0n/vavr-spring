package io.vavr.spring.convert;

import io.vavr.collection.Seq;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static io.vavr.spring.convert.VavrStringConversionUtils.isGenericTypeConvertable;

public class StringToVavrSeqConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public StringToVavrSeqConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return isGenericTypeConvertable(targetType, conversionService);
    }

    @Override
    public java.util.Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Seq.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        Stream<String> stream = Arrays.stream(
                StringUtils.commaDelimitedListToStringArray((String) source));

        return VavrStringToSeqConversionUtils.convert(stream, targetType, conversionService);
    }

}
