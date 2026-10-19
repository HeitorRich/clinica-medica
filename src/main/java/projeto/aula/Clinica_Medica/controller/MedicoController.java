package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.Consulta;
import projeto.aula.Clinica_Medica.Especialidade;
import projeto.aula.Clinica_Medica.Medico;
import projeto.aula.Clinica_Medica.service.EspecialidadeService;
import projeto.aula.Clinica_Medica.service.MedicoService;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController extends AbstractCrudController<Medico> {

    private final EspecialidadeService especialidadeService;

    public MedicoController(MedicoService service, EspecialidadeService especialidadeService) {
        super(service);
        this.especialidadeService = especialidadeService;
    }

    /** Executa Medico.consultarAgenda(). */
    @GetMapping("/{id}/agenda")
    public List<Consulta> agenda(@PathVariable int id) {
        return obrigatorio(id).consultarAgenda();
    }

    /** Executa Medico.adicionarEspecialidade(...). */
    @PostMapping("/{id}/especialidades/{especialidadeId}")
    public Medico adicionarEspecialidade(@PathVariable int id, @PathVariable int especialidadeId) {
        Medico medico = obrigatorio(id);
        Especialidade especialidade = especialidadeService.buscar(especialidadeId)
                .orElseThrow(() -> naoEncontrado(especialidadeId));
        medico.adicionarEspecialidade(especialidade);
        return service.salvar(medico);
    }

    @DeleteMapping("/{id}/especialidades/{especialidadeId}")
    public Medico removerEspecialidade(@PathVariable int id, @PathVariable int especialidadeId) {
        Medico medico = obrigatorio(id);
        medico.especialidades.removeIf(e -> e.id == especialidadeId);
        return service.salvar(medico);
    }
}
