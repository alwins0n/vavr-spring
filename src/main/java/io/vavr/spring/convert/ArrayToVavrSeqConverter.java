package io.vavr.spring.convert;

import io.vavr.collection.Seq;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Arrays;
import java.util.Collections;

import static io.vavr.spring.convert.VavrConversionUtils.isGenericTypeConvertable;

public class ArrayToVavrSeqConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public ArrayToVavrSeqConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return isGenericTypeConvertable(sourceType, targetType, conversionService);
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

        return SeqConversionUtils.convert(Arrays.stream((String[]) source),
                sourceType, targetType, conversionService);
    }

}
