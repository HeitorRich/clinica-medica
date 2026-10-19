package projeto.aula.Clinica_Medica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.Clinica_Medica.Medicamento;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {
}
