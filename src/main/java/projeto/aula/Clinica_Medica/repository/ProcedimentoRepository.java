package projeto.aula.Clinica_Medica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.Clinica_Medica.Procedimento;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Integer> {
}
