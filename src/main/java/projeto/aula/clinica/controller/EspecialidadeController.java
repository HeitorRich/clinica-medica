package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Especialidade;
import projeto.aula.clinica.domain.Medico;
import projeto.aula.clinica.service.EspecialidadeService;

import java.util.List;

/**
 * API das especialidades médicas: CRUD padrão + médicos que a possuem.
 */
@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadeController extends CrudController<Especialidade, EspecialidadeService> {

    public EspecialidadeController(EspecialidadeService service) {
        super(service);
    }

    /** Médicos vinculados a esta especialidade. */
    @GetMapping("/{id}/medicos")
    public List<Medico> medicos(@PathVariable int id) {
        return service.listarMedicos(id);
    }
}
