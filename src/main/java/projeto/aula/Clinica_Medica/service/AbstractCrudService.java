package projeto.aula.Clinica_Medica.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Serviço CRUD genérico apoiado em um repositório Spring Data JPA.
 * Os dados são persistidos no PostgreSQL pelo Hibernate (as consultas SQL
 * são geradas automaticamente pelo repositório).
 */
public abstract class AbstractCrudService<T> {

    protected final JpaRepository<T, Integer> repository;

    protected AbstractCrudService(JpaRepository<T, Integer> repository) {
        this.repository = repository;
    }

    public List<T> listar() {
        return repository.findAll();
    }

    public Optional<T> buscar(int id) {
        return repository.findById(id);
    }

    public boolean existe(int id) {
        return repository.existsById(id);
    }

    /** Insere (id <= 0) ou atualiza o registro no banco de dados. */
    public T salvar(T entidade) {
        return repository.save(entidade);
    }

    /**
     * Atualiza o registro existente alterando apenas os campos definidos em
     * {@link #copiarCampos}, preservando os vínculos (listas) e campos de situação.
     */
    public Optional<T> atualizar(int id, T entidade) {
        return repository.findById(id).map(atual -> {
            copiarCampos(entidade, atual);
            return repository.save(atual);
        });
    }

    /** Serviços sobrescrevem para copiar os campos editáveis. */
    protected void copiarCampos(T origem, T destino) {
    }

    /** Remove o registro e devolve a entidade removida (ou {@code null} se não existia). */
    public T remover(int id) {
        Optional<T> entidade = repository.findById(id);
        entidade.ifPresent(repository::delete);
        return entidade.orElse(null);
    }
}
