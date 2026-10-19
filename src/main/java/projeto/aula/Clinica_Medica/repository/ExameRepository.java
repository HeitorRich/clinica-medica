package projeto.aula.Clinica_Medica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.Clinica_Medica.Exame;

public interface ExameRepository extends JpaRepository<Exame, Integer> {
}
