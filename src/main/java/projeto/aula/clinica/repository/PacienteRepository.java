package projeto.aula.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.clinica.domain.Paciente;

import java.util.List;

/**
 * Acesso ao banco de dados para pacientes.
 */
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    /**
     * Busca pacientes pelo CPF exato (já no padrão 000.000.000-00).
     *
     * Devolve uma lista - e não um {@code Optional} - porque o banco pode
     * conter CPFs repetidos anteriores à criação desta regra de negócio.
     */
    List<Paciente> findByCpf(String cpf);
}
