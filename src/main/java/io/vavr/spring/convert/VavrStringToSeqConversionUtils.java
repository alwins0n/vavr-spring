package io.vavr.spring.convert;

import io.vavr.collection.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.util.stream.Stream;

import static io.vavr.spring.convert.VavrStringConversionUtils.toElementStream;

class VavrStringToSeqConversionUtils {

    private static final Map<Class<? extends Seq>, Class<? extends Seq>> defaultImplementations = HashMap.of(
            Seq.class, Vector.class,
            IndexedSeq.class, Vector.class,
            LinearSeq.class, List.class
    );

    static Object convert(Stream<String> source, TypeDescriptor targetType, ConversionService conversionService) {
        return collectInto(toElementStream(source, targetType, conversionService),
                getTargetCollectionType(targetType));
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Seq> getTargetCollectionType(TypeDescriptor targetType) {
        Class<? extends Seq> requestedCollectionType = (Class<? extends Seq>) targetType.getType();
        return defaultImplementations.getOrElse(requestedCollectionType, requestedCollectionType);
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
