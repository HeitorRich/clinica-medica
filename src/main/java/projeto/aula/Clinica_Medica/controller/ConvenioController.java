package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.Convenio;
import projeto.aula.Clinica_Medica.Paciente;
import projeto.aula.Clinica_Medica.PacienteConvenio;
import projeto.aula.Clinica_Medica.service.ConvenioService;

import java.util.List;

@RestController
@RequestMapping("/api/convenios")
public class ConvenioController extends AbstractCrudController<Convenio> {

    public ConvenioController(ConvenioService service) {
        super(service);
    }

    /** Executa Convenio.consultarPacientes(): operadoras podem ter vários pacientes. */
    @GetMapping("/{id}/pacientes")
    public List<Paciente> pacientes(@PathVariable int id) {
        return obrigatorio(id).consultarPacientes();
    }

    /** Vínculos (paciente + número de carteirinha) que usam este convênio. */
    @GetMapping("/{id}/vinculos")
    public List<PacienteConvenio> vinculos(@PathVariable int id) {
        return obrigatorio(id).vinculos;
    }
}
