package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.Especialidade;
import projeto.aula.Clinica_Medica.Medico;
import projeto.aula.Clinica_Medica.service.EspecialidadeService;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadeController extends AbstractCrudController<Especialidade> {

    public EspecialidadeController(EspecialidadeService service) {
        super(service);
    }

    /** Executa Especialidade.listaDeMedicos(). */
    @GetMapping("/{id}/medicos")
    public List<Medico> medicos(@PathVariable int id) {
        return obrigatorio(id).listaDeMedicos();
    }
}
