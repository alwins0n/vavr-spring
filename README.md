# Vavr-Spring

This module teaches Spring Vavr collections and `Value`s for automatic conversion. 
Vavr types can thus be used in 

- Bean configuration e.g. `@Value`
- Method parameters injected by Spring e.g. `@RequestParam`

Need a showcase?

```java
private class MyBean {
    
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
private class MyController {
    
    @RequestMapping("/my-stuff")
    public Seq<String> getMyStuff(@RequestParameter("id") Seq<Integer> ids) {
        ...
    }
}
```

## Usage

TODO