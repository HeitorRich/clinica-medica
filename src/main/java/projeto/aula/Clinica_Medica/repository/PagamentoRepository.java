package projeto.aula.Clinica_Medica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.Clinica_Medica.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
}
