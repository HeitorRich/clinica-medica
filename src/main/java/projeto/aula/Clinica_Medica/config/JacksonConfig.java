package projeto.aula.Clinica_Medica.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.cfg.ConstructorDetector;

@Configuration
public class JacksonConfig {

    /**
     * O Jackson 3 detecta automaticamente construtores com argumentos mesmo quando
     * existe um construtor padrão (DEFAULT_ALLOW_IMPLICIT_WITH_DEFAULT_CONSTRUCTOR).
     * Como as classes de domínio têm construtores com efeitos colaterais (por exemplo,
     * Consulta adiciona a si mesma ao paciente/médico), desligamos essa detecção para
     * que o JSON seja sempre lido pelo construtor padrão + campos públicos.
     */
    @Bean
    JsonMapperBuilderCustomizer constructorDetectorCustomizer() {
        return builder -> builder.constructorDetector(
                ConstructorDetector.DEFAULT.withAllowImplicitWithDefaultConstructor(false));
    }
}
