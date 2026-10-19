package projeto.aula.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.clinica.domain.Pagamento;

/**
 * Acesso ao banco de dados para a entidade Pagamento.
 * As operações CRUD (listar, buscar, salvar, remover) são herdadas do
 * JpaRepository; métodos de consulta específicos podem ser declarados aqui.
 */
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
}