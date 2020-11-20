# Vavr-Spring

This module teaches Spring Vavr collections and `Value`s for automatic conversion. 
Vavr types can thus be used in:

- Bean configuration e.g. `@Value`
- Method parameters injected by Spring e.g. `@RequestParam`

So basically, wherever a `String` or a `String[]` should be converted to Vavr Collections

Need a showcase?

```java
public class MyBean {
    
    @Value("${possibly.empty.property}")
    Option<Integer> possiblyConfiguredInt;
    
    @Value("comma,separated,values")
    Seq<String> configured;
    
    ...
}
```

or

```java
@RestController
public class MyController {
    
    @RequestMapping("/get-multiple")
    public Iterable<String> getMyStuff(@RequestParam("id") Seq<Integer> ids) {
        ...
    }
}
```

## Usage

Add vavr-spring to your maven dependencies

*TODO version*
```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr-spring</artifactId>
    <version>0.10.0-SNAPSHOT</version>
</dependency>
```

and register the converters. Depending on your environment (web/tomcat, standalone...) 
they must be registered in the appropriate `ConfigurableConversionService`.
E.g. letting spring provide it for you:

```java
@Configuration
public class MyConfig {

    ConfigurableConversionService conversionService;

    public MyConfig(ConfigurableConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostConstruct
    public void setUpConverters() {
        conversionService.addConverter(new StringToVavrCollectionConverter(conversionService));
        ...
    }
}
```

The following converters are available to register:
- `StringToVavrSeqConverter`
- `StringArrayToVavrSeqConverter`
- `StringToVavrSetConverter`
- `StringArrayToVavrSetConverter`
- `StringToOptionConverter`

They can also conveniently be set up by calling 

```java
VavrConverters.registerAll(conversionService);
```

## Limitations

In web applications a request mapped method with argument of type `Option<T>` will not
be resolved properly. Support for `java.util.Optional<T>` is build-in and hard-wired
into Spring. In this case it is recommended to use optional parameters as such

```java
@RestController
public class MyController {
    
    @RequestMapping("/optional-param")
    public Object getByName(@RequestParam(value = "id", required = false) Optional<String> name) {
        ... Option.ofOptional(name).map(n -> ...)
    }
}
```

## None-Scope

j.u.Collections to io.vavr.Collections are not covered. 
They are mostly needed for internal/data-access conversions and are implemented in
spring-data-commons https://github.com/spring-projects/spring-data-commons and 
thus part of spring-data-jpa 

## TODO 

- Array syntax binding
- Map based binding
- YAML, JSON

see https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-Configuration-Binding 