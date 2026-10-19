package projeto.aula.Clinica_Medica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.Clinica_Medica.Paciente;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    /**
     * Busca pacientes pelo CPF exato. Devolve uma lista (e não Optional) porque
     * o banco pode conter registros repetidos anteriores à criação desta regra.
     */
    List<Paciente> findByCpf(String cpf);
}
