package projeto.aula.clinica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import projeto.aula.clinica.domain.EntidadeBase;
import projeto.aula.clinica.exception.RecursoNaoEncontradoException;
import projeto.aula.clinica.service.CrudService;

import java.util.List;

/**
 * Controller base com as cinco operações CRUD.
 *
 * Cada controller concreto informa apenas a rota (ex.: {@code /api/pacientes}),
 * o serviço e, quando existir, os endpoints extras da sua entidade. Toda a
 * conversa HTTP comum (listar, buscar, criar, atualizar, remover) fica aqui.
 *
 * @param <T> entidade exposta pela API
 * @param <S> serviço que implementa as regras dessa entidade
 */
public abstract class CrudController<T extends EntidadeBase, S extends CrudService<T>> {

    protected final S service;

    protected CrudController(S service) {
        this.service = service;
    }

    @GetMapping
    public List<T> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public T buscar(@PathVariable int id) {
        return service.buscarObrigatorio(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T criar(@RequestBody T entidade) {
        return service.salvar(entidade);
    }

    @PutMapping("/{id}")
    public T atualizar(@PathVariable int id, @RequestBody T entidade) {
        return service.atualizar(id, entidade)
                .orElseThrow(() -> RecursoNaoEncontradoException.paraRegistro(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable int id) {
        service.remover(id);
    }
}
