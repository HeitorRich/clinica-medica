package projeto.aula.clinica;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Teste de fumaça: garante que o contexto Spring sobe com todas as camadas
 * (repositories, services e controllers) e que o banco está acessível.
 */
@SpringBootTest
class ClinicaMedicaApplicationTests {

    @Test
    void contextLoads() {
    }
}
