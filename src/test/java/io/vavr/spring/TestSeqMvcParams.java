package io.vavr.spring;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.spring.propertyeditors.ArrayToVavrSeqConverter;
import io.vavr.spring.propertyeditors.StringToVavrSeqConverter;
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
@SpringJUnitWebConfig(classes = TestSeqMvcParams.TestConfig.class)
public class TestSeqMvcParams {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TestSeqController seqController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    // tests for seq interface
    @Test
    public void testMvcArg_fromRequestParams_shouldBeSeq() throws Exception {
        seqController.setAssertion(param -> assertEquals(Vector.of(1, 2), param));
        mockMvc.perform(get("/seq?value=1&value=2"))
                .andExpect(status().isOk());
    }

    @Test
    public void testMvcArg_fromEmptyString_shouldBeEmptySeq() throws Exception {
        seqController.setAssertion(param -> assertEquals(Vector.empty(), param));
        mockMvc.perform(get("/seq?value="))
                .andExpect(status().isOk());
    }

    @Test
    public void testMvcArg_fromSingleParamString_shouldBeNoneEmptySeq() throws Exception {
        seqController.setAssertion(param -> assertEquals(Vector.of(1), param));
        mockMvc.perform(get("/seq?value=1"))
                .andExpect(status().isOk());
    }

    // tests for concrete subtype
    @Test
    public void testMvcArg_fromRequestParams_shouldBeList() throws Exception {
        seqController.setAssertion(param -> assertEquals(List.of(1, 2), param));
        mockMvc.perform(get("/list?value=1&value=2"))
                .andExpect(status().isOk());
    }

    @Test
    public void testMvcArg_fromEmptyString_shouldBeEmptyList() throws Exception {
        seqController.setAssertion(param -> assertEquals(List.empty(), param));
        mockMvc.perform(get("/list?value="))
                .andExpect(status().isOk());
    }

    @Test
    public void testMvcArg_fromSingleParamString_shouldBeNoneEmptyList() throws Exception {
        seqController.setAssertion(param -> assertEquals(List.of(1), param));
        mockMvc.perform(get("/list?value=1"))
                .andExpect(status().isOk());
    }

    @Configuration
    @EnableWebMvc
    @ComponentScan(
            basePackages = "io.vavr.spring",
            includeFilters = @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    value = TestSeqController.class))
    static class TestConfig implements WebMvcConfigurer {


        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new StringToVavrSeqConverter((ConversionService) registry));
            registry.addConverter(new ArrayToVavrSeqConverter((ConversionService) registry));
        }
    }

    @RestController
    private static class TestSeqController {

        private Consumer<Seq<Integer>> assertion;

        void setAssertion(Consumer<Seq<Integer>> assertion) {
            this.assertion = assertion;
        }

        @GetMapping("/seq")
        public void shouldBeSeq(@RequestParam("value") Seq<Integer> param) {
            assertion.accept(param);
        }

        @GetMapping("/list")
        public void shouldBeList(@RequestParam("value") List<Integer> param) {
            assertion.accept(param);
        }
    }

}
