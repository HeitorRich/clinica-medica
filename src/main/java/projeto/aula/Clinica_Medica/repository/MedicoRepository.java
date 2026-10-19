package projeto.aula.Clinica_Medica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.Clinica_Medica.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Integer> {
}
