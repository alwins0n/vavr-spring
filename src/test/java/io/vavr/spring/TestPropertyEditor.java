package io.vavr.spring;

import io.vavr.collection.*;
import io.vavr.control.Option;
import io.vavr.spring.propertyeditors.StringToOptionConverter;
import io.vavr.spring.propertyeditors.StringToVavrSeqConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPropertyEditor.TestConfig.class)
public class TestPropertyEditor {

    @Autowired
    private TestBean bean;

    @Test
    public void testProperties_fromString_shouldBeOptions() {
        assertEquals(Option.none(), bean.shouldBeNone);
        assertEquals(Option.some(1), bean.shouldBeSomeOne);
        assertEquals(Option.some(Option.some(1)), bean.shouldBeNestedOne);
    }

    @Test
    public void testProperties_fromString_shouldBeSeqs() {
        assertEquals(Vector.of(1, 2, 3), bean.shouldBeSeq);
        assertEquals(Vector.empty(), bean.shouldBeEmptySeq);
        assertEquals(List.of(1, 2, 3), bean.shouldBeList);
        assertEquals(Vector.of(1, 2, 3), bean.shouldBeVector);
        assertEquals(List.of(1, 2, 3), bean.shouldBeLinearSeq);
        assertEquals(List.of(1, 2, 3), bean.shouldBeIndexedSeq);
        assertEquals(Queue.of(1, 2, 3), bean.shouldBeQueue);
        assertEquals(Array.of(1, 2, 3), bean.shouldBeArray);
    }

    @Configuration
    static class TestConfig {

        @Autowired
        ConfigurableEnvironment environment;

        @Bean
        public ConfigurableConversionService conversionService() {
            final DefaultConversionService conversionService = new DefaultConversionService();
            conversionService.addConverter(new StringToOptionConverter(conversionService));
            conversionService.addConverter(new StringToVavrSeqConverter(conversionService));
            return conversionService;
        }

        /* TODO make this work
               @PostConstruct
               public void addConverters() {
                   ConfigurableConversionService conversionService = environment.getConversionService();
                   conversionService.addConverter(new StringToOptionConverter(conversionService));
                   conversionService.addConverter(new StringToVavrSeqConverter(conversionService));
               }
        */
        @Bean
        public TestBean testBean() {
            return new TestBean();
        }
    }

    private static class TestBean {
        @Value("")
        Option<Integer> shouldBeNone;
        @Value("1")
        Option<Integer> shouldBeSomeOne;
        @Value("1")
        Option<Option<Integer>> shouldBeNestedOne;

        @Value("1,2,3")
        Seq<Integer> shouldBeSeq;
        @Value("")
        Seq<Integer> shouldBeEmptySeq;
        @Value("1,2,3")
        List<Integer> shouldBeList;
        @Value("1,2,3")
        Vector<Integer> shouldBeVector;
        @Value("1,2,3")
        LinearSeq<Integer> shouldBeLinearSeq;
        @Value("1,2,3")
        IndexedSeq<Integer> shouldBeIndexedSeq;
        @Value("1,2,3")
        Queue<Integer> shouldBeQueue;
        @Value("1,2,3")
        Array<Integer> shouldBeArray;
    }

}
