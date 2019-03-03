package io.vavr.spring.convert;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.collection.TreeSet;
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

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitWebConfig(classes = TestSetMvcParams.TestConfig.class)
public class TestSetMvcParams {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TestSetController setController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    // tests for Set interface
    @Test
    public void testMvcArg_fromRequestParams_shouldBeSet() throws Exception {
        setController.setAssertion(param -> assertEquals(HashSet.of(1, 2), param));
        mockMvc.perform(get("/set?value=1&value=2&value=1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testMvcArg_fromEmptyString_shouldBeEmptySet() throws Exception {
        setController.setAssertion(param -> assertEquals(HashSet.empty(), param));
        mockMvc.perform(get("/set?value="))
                .andExpect(status().isOk());
    }

    @Test
    public void testMvcArg_fromSingleParamString_shouldBeNoneEmptySet() throws Exception {
        setController.setAssertion(param -> assertEquals(HashSet.of(1), param));
        mockMvc.perform(get("/set?value=1"))
                .andExpect(status().isOk());
    }

    // tests for concrete subtype
    @Test
    public void testMvcArg_fromRequestParams_shouldBeTreeSet() throws Exception {
        setController.setAssertion(param -> assertEquals(TreeSet.of(1, 2), param));
        mockMvc.perform(get("/tree-set?value=1&value=2&value=1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testMvcArg_fromEmptyString_shouldBeEmptyTreeSet() throws Exception {
        setController.setAssertion(param -> assertEquals(TreeSet.empty(), param));
        mockMvc.perform(get("/tree-set?value="))
                .andExpect(status().isOk());
    }

    @Test
    public void testMvcArg_fromSingleParamString_shouldBeNoneEmptyTreeSet() throws Exception {
        setController.setAssertion(param -> assertEquals(TreeSet.of(1), param));
        mockMvc.perform(get("/tree-set?value=1"))
                .andExpect(status().isOk());
    }

    @Configuration
    @EnableWebMvc
    @ComponentScan(
            basePackages = "io.vavr.spring",
            includeFilters = @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    value = TestSetController.class))
    static class TestConfig implements WebMvcConfigurer {


        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new StringToVavrSetConverter((ConversionService) registry));
            registry.addConverter(new StringArrayToVavrSetConverter((ConversionService) registry));
        }
    }

    @RestController
    private static class TestSetController {

        private Consumer<Set<Integer>> assertion;

        void setAssertion(Consumer<Set<Integer>> assertion) {
            this.assertion = assertion;
        }

        @GetMapping("/set")
        public void shouldBeSet(@RequestParam("value") Set<Integer> param) {
            assertion.accept(param);
        }

        @GetMapping("/tree-set")
        public void shouldBeTreeSet(@RequestParam("value") TreeSet<Integer> param) {
            assertion.accept(param);
        }
    }

}
