package io.vavr.spring.convert;

import io.vavr.control.Option;
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
@ContextConfiguration(classes = TestOptionPropertyInjection.TestConfig.class)
public class TestOptionPropertyInjection {

    @Autowired
    private TestBean bean;

    @Test
    public void testProperties_fromEmptyString_shouldBeNone() {
        assertEquals(Option.none(), bean.shouldBeNone);
    }

    @Test
    public void testProperties_fromString_shouldBePresentOption() {
        assertEquals(Option.some(1), bean.shouldBeSomeOne);
    }

    @Test
    public void testProperties_fromString_shouldBeNestedPresentOption() {
        assertEquals(Option.some(Option.some(1)), bean.shouldBeNestedOne);
    }

    @Test
    public void testStringProperty_shouldBePresentOption() {
        assertEquals(Option.some("1"), bean.shouldBeSomeString);
    }

    @Configuration
    static class TestConfig {

        @Bean
        public ConfigurableConversionService conversionService(ConfigurableEnvironment environment) {
            final ConfigurableConversionService conversionService = environment.getConversionService();
            conversionService.addConverter(new StringToOptionConverter(conversionService));
            return conversionService;
        }

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
        @Value("1")
        Option<String> shouldBeSomeString;
    }

}
