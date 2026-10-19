package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Convenio;
import projeto.aula.clinica.domain.Paciente;
import projeto.aula.clinica.domain.PacienteConvenio;
import projeto.aula.clinica.service.ConvenioService;

import java.util.List;

/**
 * API das operadoras (convênios): CRUD padrão + pacientes atendidos e vínculos.
 */
@RestController
@RequestMapping("/api/convenios")
public class ConvenioController extends CrudController<Convenio, ConvenioService> {

    public ConvenioController(ConvenioService service) {
        super(service);
    }

    /** Pacientes que utilizam esta operadora. */
    @GetMapping("/{id}/pacientes")
    public List<Paciente> pacientes(@PathVariable int id) {
        return service.listarPacientes(id);
    }

    /** Vínculos (paciente + número de carteirinha) desta operadora. */
    @GetMapping("/{id}/vinculos")
    public List<PacienteConvenio> vinculos(@PathVariable int id) {
        return service.listarVinculos(id);
    }
}
