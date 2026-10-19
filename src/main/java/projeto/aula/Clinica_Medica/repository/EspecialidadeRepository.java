package projeto.aula.Clinica_Medica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.Clinica_Medica.Especialidade;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Integer> {
}
