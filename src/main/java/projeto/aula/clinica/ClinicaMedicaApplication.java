package projeto.aula.clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação (Clínica Médica).
 *
 * A estrutura do projeto segue as camadas:
 * <ul>
 *   <li>{@code domain}      - entidades JPA e regras que dependem de uma única entidade;</li>
 *   <li>{@code repository}  - acesso ao banco (Spring Data JPA);</li>
 *   <li>{@code service}     - regras de negócio e transações;</li>
 *   <li>{@code controller}  - API REST (entrada/saída HTTP);</li>
 *   <li>{@code config}      - configurações de infraestrutura (serialização, dados iniciais);</li>
 *   <li>{@code exception}   - exceções de domínio tratadas pela API.</li>
 * </ul>
 */
@SpringBootApplication
public class ClinicaMedicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicaMedicaApplication.class, args);
    }
}
