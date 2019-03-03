package io.vavr.spring.convert;

import io.vavr.collection.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.util.stream.Stream;

import static io.vavr.spring.convert.VavrStringConversionUtils.toElementStream;

class VavrStringToSetConversionUtils {

    private static final Map<Class<? extends Set>, Class<? extends Set>> defaultImplementations = HashMap.of(
            Set.class, HashSet.class,
            SortedSet.class, TreeSet.class
    );

    // move?
    static Object convert(Stream<String> source, TypeDescriptor targetType,
            ConversionService conversionService) {
        return collectInto(toElementStream(source, targetType, conversionService),
                getTargetCollectionType(targetType));
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Set> getTargetCollectionType(TypeDescriptor targetType) {
        Class<? extends Set> requestedCollectionType = (Class<? extends Set>) targetType.getType();
        return defaultImplementations.getOrElse(requestedCollectionType, requestedCollectionType);
    }

    @SuppressWarnings("unchecked")
    private static Object collectInto(Stream<?> stream, Class<? extends Set> targetCollection) {
        if (HashSet.class.equals(targetCollection)) {
            return stream.collect(HashSet.collector());
        } else if (LinkedHashSet.class.equals(targetCollection)) {
            return stream.collect(LinkedHashSet.collector());
        } else if (TreeSet.class.equals(targetCollection)) {
            return ((Stream<? extends Comparable<?>>) stream).collect(TreeSet.<Comparable>collector());
        }

        throw new UnsupportedOperationException("cannot collect into " + targetCollection
                + ", maybe that type does not extend io.vavr.Set");
    }


}
