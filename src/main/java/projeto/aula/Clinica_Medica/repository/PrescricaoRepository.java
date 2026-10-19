package projeto.aula.Clinica_Medica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.Clinica_Medica.Prescricao;

public interface PrescricaoRepository extends JpaRepository<Prescricao, Integer> {
}
