package projeto.aula.clinica.service;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.aula.clinica.domain.EntidadeBase;
import projeto.aula.clinica.exception.RecursoNaoEncontradoException;

import java.util.List;
import java.util.Optional;

/**
 * Serviço CRUD genérico.
 *
 * Concentra as operações comuns a todas as entidades para que cada serviço
 * concreto cuide apenas das suas regras de negócio. É o único lugar que fala
 * diretamente com o repositório no que diz respeito ao CRUD básico.
 *
 * @param <T> entidade persistida gerenciada por este serviço
 */
public abstract class CrudService<T extends EntidadeBase> {

    /** Repositório da entidade; usado pelos serviços concretos quando precisam de consultas próprias. */
    protected final JpaRepository<T, Integer> repository;

    protected CrudService(JpaRepository<T, Integer> repository) {
        this.repository = repository;
    }

    public List<T> listar() {
        return repository.findAll();
    }

    public Optional<T> buscar(int id) {
        return repository.findById(id);
    }

    /** Igual a {@link #buscar(int)}, mas lança 404 quando o registro não existe. */
    public T buscarObrigatorio(int id) {
        return repository.findById(id).orElseThrow(() -> RecursoNaoEncontradoException.paraRegistro(id));
    }

    /** Insere (id não informado) ou atualiza o registro no banco de dados. */
    public T salvar(T entidade) {
        return repository.save(entidade);
    }

    /**
     * Atualiza o registro existente copiando apenas os campos editáveis
     * (ver {@link #copiarCampos}), preservando vínculos, coleções e campos de
     * situação. Devolve vazio quando o registro não existe.
     */
    public Optional<T> atualizar(int id, T dados) {
        return repository.findById(id).map(atual -> {
            copiarCampos(dados, atual);
            return repository.save(atual);
        });
    }

    /** Remove o registro; lança 404 quando ele não existe. */
    public void remover(int id) {
        repository.delete(buscarObrigatorio(id));
    }

    /**
     * Ponto de extensão da atualização: copia do objeto recebido no JSON para
     * o registro que está no banco apenas os campos que o cliente pode alterar.
     *
     * Cada serviço concreto declara explicitamente essa lista, o que evita que
     * uma atualização parcial apague dados (ex.: zerar a situação de um
     * pagamento) - comportamento que existia antes e foi mantido.
     */
    protected void copiarCampos(T origem, T destino) {
        // por padrão nada é copiado; veja os serviços concretos
    }
}
