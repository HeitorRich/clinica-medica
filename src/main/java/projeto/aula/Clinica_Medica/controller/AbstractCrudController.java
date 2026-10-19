package projeto.aula.Clinica_Medica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import projeto.aula.Clinica_Medica.service.AbstractCrudService;

import java.util.List;

/**
 * Controller base com as operações CRUD.
 * Cada controller concreto informa o caminho (ex.: /api/pacientes) e o serviço utilizado.
 */
public abstract class AbstractCrudController<T> {

    protected final AbstractCrudService<T> service;

    protected AbstractCrudController(AbstractCrudService<T> service) {
        this.service = service;
    }

    @GetMapping
    public List<T> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public T buscar(@PathVariable int id) {
        return obrigatorio(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T criar(@RequestBody T entidade) {
        return service.salvar(entidade);
    }

    @PutMapping("/{id}")
    public T atualizar(@PathVariable int id, @RequestBody T entidade) {
        return service.atualizar(id, entidade).orElseThrow(() -> naoEncontrado(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable int id) {
        if (service.remover(id) == null) {
            throw naoEncontrado(id);
        }
    }

    protected T obrigatorio(int id) {
        return service.buscar(id).orElseThrow(() -> naoEncontrado(id));
    }

    protected ResponseStatusException naoEncontrado(int id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro " + id + " não encontrado");
    }
}
