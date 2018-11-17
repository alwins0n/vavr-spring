package io.vavr.spring.propertyeditors;

import io.vavr.NotImplementedError;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class StringToVavrSetConverter implements ConditionalGenericConverter {

    private final Map<Class<? extends Set>, Class<? extends Set>> defaultImplementations = HashMap.of(
            Set.class, HashSet.class,
            SortedSet.class, TreeSet.class
    );

    private final ConversionService conversionService;

    public StringToVavrSetConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Option<SingleGenericTypeDescriptor> elementDesc =
                SingleGenericTypeDescriptor.resolve(targetType);

        return elementDesc
                .map(e -> this.conversionService.canConvert(sourceType, e))
                .getOrElse(true);
    }

    @Override
    public java.util.Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Set.class));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        Stream<String> stream = Arrays.stream(
                StringUtils.commaDelimitedListToStringArray((String) source));

        Class<? extends Set> requestedCollectionType = (Class<? extends Set>) targetType.getType();
        Class<? extends Set> targetCollectionType = defaultImplementations
                .getOrElse(requestedCollectionType, requestedCollectionType);

        Option<SingleGenericTypeDescriptor> elementDesc =
                SingleGenericTypeDescriptor.resolve(targetType);

        Stream<?> elementStream = elementDesc.isDefined() ?
                stream.map(f -> getElement(f, sourceType, elementDesc.get())) :
                stream;

        return collectInto(elementStream, targetCollectionType);
    }

    private Object collectInto(Stream<?> stream, Class<? extends Set> targetCollection) {
        if (HashSet.class.equals(targetCollection)) {
            return stream.collect(HashSet.collector());
        } else if (LinkedHashSet.class.equals(targetCollection)) {
            return stream.collect(LinkedHashSet.collector());
        } else if (TreeSet.class.equals(targetCollection)) {
            // TODO
            throw new NotImplementedError();
        }

        throw new UnsupportedOperationException("cannot collect into " + targetCollection
                + ", maybe that type does not extend io.vavr.Set");
    }


    private Object getElement(String from, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.convert(from.trim(), sourceType, targetType);
    }

}
