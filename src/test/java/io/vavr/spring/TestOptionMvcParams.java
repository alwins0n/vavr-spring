package io.vavr.spring;

import io.vavr.control.Option;
import io.vavr.spring.propertyeditors.StringToOptionConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitWebConfig(classes = TestOptionMvcParams.TestConfig.class)
//@ContextConfiguration(classes = TestOptionMvcParams.TestConfig.class)
public class TestOptionMvcParams {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        //mockMvc = MockMvcBuilders.standaloneSetup(new TestOptionController()).build();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testMvcArg_fromEmptyString_shouldBeNone() throws Exception {
        mockMvc.perform(get("/none"));
    }

    @Test
    public void testMvcArg_requiredFromEmptyString_shouldBeNone() throws Exception {
        mockMvc.perform(get("/nonereq"));
    }

    @Test
    public void testMvcArg_fromString_shouldBePresentOption() throws Exception {
        mockMvc.perform(get("/someone?value=1"));
    }
/*
    @Test
    public void testProperties_fromString_shouldBeNestedPresentOption() {
        assertEquals(Option.some(Option.some(1)), wac.shouldBeNestedOne);
    }

    @Test
    public void testStringProperty_shouldBePresentOption() {
        assertEquals(Option.some("1"), wac.shouldBeSomeString);
    }*/

    @Configuration
    @EnableWebMvc
    @ComponentScan(basePackages = "io.vavr.spring", includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TestOptionController.class))
    static class TestConfig implements WebMvcConfigurer {


        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new StringToOptionConverter((ConversionService) registry));
        }

        /* @Bean
        public TestOptionController testBean() {
            return new TestOptionController();
        }*/
    }

    @RestController
    private static class TestOptionController {
        @GetMapping("/none")
        public void shouldBeNone(@RequestParam(value = "value", required = false) Option<Integer> param) {
            System.out.println(param);
        }

        @GetMapping("/nonereq")
        public void shouldBeNoneRequired(@RequestParam("value") Option<Integer> param) {
            System.out.println(param);
        }

        @GetMapping("/someone")
        public void shouldBeSomeOne(@RequestParam("value") Option<Integer> param) {
            System.out.println(param);
        }
/*
        @Value("1")
        Option<Integer> shouldBeSomeOne;
        @Value("1")
        Option<Option<Integer>> shouldBeNestedOne;
        @Value("1")
        Option<String> shouldBeSomeString;*/
    }

}
