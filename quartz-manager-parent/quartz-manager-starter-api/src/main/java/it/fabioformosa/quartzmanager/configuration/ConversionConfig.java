package it.fabioformosa.quartzmanager.configuration;

import it.fabioformosa.metamorphosis.core.EnableMetamorphosisConversions;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMetamorphosisConversions(basePackages = { "it.fabioformosa.quartzmanager" })
public class ConversionConfig {
}
