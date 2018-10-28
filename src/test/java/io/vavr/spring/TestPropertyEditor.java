package io.vavr.spring;

import io.vavr.control.Option;
import io.vavr.spring.propertyeditors.VavrEditorRegistrar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        assertEquals(Option.none(), bean.getShouldBeNone());
        assertEquals(Option.some(1), bean.getShouldBeSomeOne());
    }

    @Configuration
    static class TestConfig {

        @Bean
        public CustomEditorConfigurer customEditorConfigurer() {
            CustomEditorConfigurer cec = new CustomEditorConfigurer();
            cec.setPropertyEditorRegistrars(new PropertyEditorRegistrar[]{
                    new VavrEditorRegistrar()
            });
            return cec;
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

        public Option<Integer> getShouldBeNone() {
            return shouldBeNone;
        }

        public Option<Integer> getShouldBeSomeOne() {
            return shouldBeSomeOne;
        }
    }

}
