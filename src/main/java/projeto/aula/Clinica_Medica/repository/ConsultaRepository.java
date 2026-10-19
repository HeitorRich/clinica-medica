package projeto.aula.Clinica_Medica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.Clinica_Medica.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
}
