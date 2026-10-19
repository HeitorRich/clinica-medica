package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Consulta;
import projeto.aula.clinica.domain.Medico;
import projeto.aula.clinica.service.MedicoService;

import java.util.List;

/**
 * API do médico: CRUD padrão + agenda e especialidades.
 */
@RestController
@RequestMapping("/api/medicos")
public class MedicoController extends CrudController<Medico, MedicoService> {

    public MedicoController(MedicoService service) {
        super(service);
    }

    /** Consultas agendadas para o médico. */
    @GetMapping("/{id}/agenda")
    public List<Consulta> agenda(@PathVariable int id) {
        return service.consultarAgenda(id);
    }

    /** Vincula uma especialidade do catálogo ao médico. */
    @PostMapping("/{id}/especialidades/{especialidadeId}")
    public Medico adicionarEspecialidade(@PathVariable int id, @PathVariable int especialidadeId) {
        return service.adicionarEspecialidade(id, especialidadeId);
    }

    /** Desvincula a especialidade do médico. */
    @DeleteMapping("/{id}/especialidades/{especialidadeId}")
    public Medico removerEspecialidade(@PathVariable int id, @PathVariable int especialidadeId) {
        return service.removerEspecialidade(id, especialidadeId);
    }
}
