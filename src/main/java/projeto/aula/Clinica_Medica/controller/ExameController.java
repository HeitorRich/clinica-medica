package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.Exame;
import projeto.aula.Clinica_Medica.Paciente;
import projeto.aula.Clinica_Medica.ResultadoExame;
import projeto.aula.Clinica_Medica.service.ExameService;
import projeto.aula.Clinica_Medica.service.ResultadoExameService;

@RestController
@RequestMapping("/api/exames")
public class ExameController extends AbstractCrudController<Exame> {

    private final ResultadoExameService resultadoExameService;

    public ExameController(ExameService service, ResultadoExameService resultadoExameService) {
        super(service);
        this.resultadoExameService = resultadoExameService;
    }

    /** Executa Exame.registrarResultado(...). */
    @PostMapping("/{id}/resultado")
    public Exame registrarResultado(@PathVariable int id, @RequestBody ResultadoExame resultado) {
        Exame exame = obrigatorio(id);
        resultadoExameService.salvar(resultado);
        exame.registrarResultado(resultado);
        return service.salvar(exame);
    }

    /** Executa Exame.consultarPaciente(): paciente dono do exame (via consulta de origem). */
    @GetMapping("/{id}/paciente")
    public Paciente paciente(@PathVariable int id) {
        return obrigatorio(id).consultarPaciente();
    }
}
