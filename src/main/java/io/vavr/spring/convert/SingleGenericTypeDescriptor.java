package io.vavr.spring.convert;

import io.vavr.control.Option;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.annotation.Annotation;

class SingleGenericTypeDescriptor extends TypeDescriptor {

    private static final long serialVersionUID = 1L;

    private SingleGenericTypeDescriptor(ResolvableType genericType, Annotation[] annotations) {
        super(genericType, null, annotations);
    }

    static Option<SingleGenericTypeDescriptor> resolve(TypeDescriptor typeDescriptor) {
        if (typeDescriptor.getResolvableType().getGenerics().length != 1) {
            throw new IllegalArgumentException("type has more than a single generic type");
        }

        ResolvableType genericType = typeDescriptor.getResolvableType().getGeneric(0);
        if (genericType == null) {
            return Option.none();
        }

        return Option.of(new SingleGenericTypeDescriptor(genericType,
                typeDescriptor.getAnnotations()));
    }
}
