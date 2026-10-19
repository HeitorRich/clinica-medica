package projeto.aula.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.clinica.domain.Consulta;

/**
 * Acesso ao banco de dados para a entidade Consulta.
 * As operações CRUD (listar, buscar, salvar, remover) são herdadas do
 * JpaRepository; métodos de consulta específicos podem ser declarados aqui.
 */
public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
}