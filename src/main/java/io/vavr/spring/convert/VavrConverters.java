package io.vavr.spring.convert;

import org.springframework.core.convert.support.ConfigurableConversionService;

public class VavrConverters {

    /**
     * convenience method to register all available vavr converters, i.e
     * <p>
     * - StringToVavrSeqConverter
     * - StringArrayToVavrSeqConverter
     * - StringToVavrSetConverter
     * - StringArrayToVavrSetConverter
     * - StringToOptionConverter
     *
     * @param conversionService the conversion service registered to
     */
    public static void registerAll(ConfigurableConversionService conversionService) {
        conversionService.addConverter(new StringArrayToVavrSeqConverter(conversionService));
        conversionService.addConverter(new StringArrayToVavrSetConverter(conversionService));
        conversionService.addConverter(new StringToOptionConverter(conversionService));
        conversionService.addConverter(new StringToVavrSeqConverter(conversionService));
        conversionService.addConverter(new StringToVavrSetConverter(conversionService));
    }

}
