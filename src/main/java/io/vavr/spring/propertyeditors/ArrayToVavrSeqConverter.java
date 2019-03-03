package io.vavr.spring.propertyeditors;

import io.vavr.collection.*;
import io.vavr.control.Option;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class ArrayToVavrSeqConverter implements ConditionalGenericConverter {

    private final Map<Class<? extends Seq>, Class<? extends Seq>> defaultImplementations = HashMap.of(
            Seq.class, Vector.class,
            IndexedSeq.class, Vector.class,
            LinearSeq.class, List.class
    );

    private final ConversionService conversionService;

    public ArrayToVavrSeqConverter(ConversionService conversionService) {
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
        return Collections.singleton(new ConvertiblePair(String[].class, Seq.class));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        Stream<Object> stream = Arrays.stream((Object[]) source);

        Class<? extends Seq> requestedCollectionType = (Class<? extends Seq>) targetType.getType();
        Class<? extends Seq> targetCollectionType = defaultImplementations
                .getOrElse(requestedCollectionType, requestedCollectionType);

        Option<SingleGenericTypeDescriptor> elementDesc =
                SingleGenericTypeDescriptor.resolve(targetType);

        Stream<?> elementStream = elementDesc.isDefined() ?
                stream.map(f -> getElement(f, sourceType.elementTypeDescriptor(f), elementDesc.get())) :
                stream;

        return collectInto(elementStream, targetCollectionType);
    }

    private Object collectInto(Stream<?> stream, Class<? extends Seq> targetCollection) {
        if (Array.class.equals(targetCollection)) {
            return stream.collect(Array.collector());
        } else if (List.class.equals(targetCollection)) {
            return stream.collect(List.collector());
        } else if (Vector.class.equals(targetCollection)) {
            return stream.collect(Vector.collector());
        } else if (Queue.class.equals(targetCollection)) {
            return stream.collect(Queue.collector());
        }

        throw new UnsupportedOperationException("cannot collect into " + targetCollection
                + ", maybe that type does not extend io.vavr.Seq");
    }


    private Object getElement(Object from, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.convert(from, sourceType, targetType);
    }

}
