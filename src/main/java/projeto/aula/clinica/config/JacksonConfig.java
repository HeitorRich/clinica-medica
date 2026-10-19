package projeto.aula.clinica.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.cfg.ConstructorDetector;

/**
 * Ajustes de serialização/desserialização JSON.
 */
@Configuration
public class JacksonConfig {

    /**
     * O Jackson 3 detecta automaticamente construtores com argumentos mesmo
     * quando existe um construtor padrão
     * (DEFAULT_ALLOW_IMPLICIT_WITH_DEFAULT_CONSTRUCTOR).
     *
     * Como as entidades de domínio têm construtores com efeitos colaterais
     * (por exemplo, Consulta adiciona a si mesma ao paciente/médico), desligamos
     * essa detecção: o JSON é sempre lido pelo construtor padrão + setters.
     */
    @Bean
    JsonMapperBuilderCustomizer constructorDetectorCustomizer() {
        return builder -> builder.constructorDetector(
                ConstructorDetector.DEFAULT.withAllowImplicitWithDefaultConstructor(false));
    }
}
