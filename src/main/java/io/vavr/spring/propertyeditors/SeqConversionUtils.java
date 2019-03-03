package io.vavr.spring.propertyeditors;

import io.vavr.collection.*;
import io.vavr.control.Option;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.util.stream.Stream;

class SeqConversionUtils {

    private static final Map<Class<? extends Seq>, Class<? extends Seq>> defaultImplementations = HashMap.of(
            Seq.class, Vector.class,
            IndexedSeq.class, Vector.class,
            LinearSeq.class, List.class
    );

    @SuppressWarnings("unchecked")
    static Object convert(Stream<String> source, TypeDescriptor sourceType, TypeDescriptor targetType, ConversionService conversionService) {
        Class<? extends Seq> requestedCollectionType = (Class<? extends Seq>) targetType.getType();
        Class<? extends Seq> targetCollectionType = defaultImplementations
                .getOrElse(requestedCollectionType, requestedCollectionType);

        Option<SingleGenericTypeDescriptor> elementDesc =
                SingleGenericTypeDescriptor.resolve(targetType);

        Stream<?> elementStream = elementDesc.isDefined() ?
                source.map(f -> conversionService.convert(f, sourceType.elementTypeDescriptor(f), elementDesc.get())) :
                source;

        return collectInto(elementStream, targetCollectionType);
    }

    private static Object collectInto(Stream<?> stream, Class<? extends Seq> targetCollection) {
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


}
