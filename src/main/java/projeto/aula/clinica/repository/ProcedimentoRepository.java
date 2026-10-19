package projeto.aula.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.clinica.domain.Procedimento;

/**
 * Acesso ao banco de dados para a entidade Procedimento.
 * As operações CRUD (listar, buscar, salvar, remover) são herdadas do
 * JpaRepository; métodos de consulta específicos podem ser declarados aqui.
 */
public interface ProcedimentoRepository extends JpaRepository<Procedimento, Integer> {
}