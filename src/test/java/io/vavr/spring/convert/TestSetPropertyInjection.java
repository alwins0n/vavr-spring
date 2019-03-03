package io.vavr.spring.convert;

import io.vavr.collection.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestSetPropertyInjection.TestConfig.class)
public class TestSetPropertyInjection {

    @Autowired
    private TestBean bean;

    @Test
    public void testProperties_fromString_shouldBeSets() {
        assertEquals(HashSet.of(1, 2, 3), bean.shouldBeSet);
        assertEquals(HashSet.empty(), bean.shouldBeEmptySet);
        assertEquals(HashSet.of(1, 2, 3), bean.shouldBeHashSet);
        assertEquals(LinkedHashSet.of(1, 2, 3), bean.shouldBeLinkedHashSet);
    }

    @Configuration
    static class TestConfig {

        @Bean
        public ConfigurableConversionService conversionService(ConfigurableEnvironment environment) {
            final ConfigurableConversionService conversionService = environment.getConversionService();
            conversionService.addConverter(new StringToVavrSetConverter(conversionService));
            return conversionService;
        }

        @Bean
        public TestBean testBean() {
            return new TestBean();
        }
    }

    private static class TestBean {
        @Value("1,2,3")
        Set<Integer> shouldBeSet;
        @Value("")
        Set<Integer> shouldBeEmptySet;
        @Value("1,2,3")
        HashSet<Integer> shouldBeHashSet;
        @Value("1,2,3")
        LinkedHashSet<Integer> shouldBeLinkedHashSet;
        @Value("1,2,3")
        SortedSet<Integer> shouldBeSortedSet;
        @Value("1,2,3")
        TreeSet<Integer> shouldBeTreeSet;

    }

}
