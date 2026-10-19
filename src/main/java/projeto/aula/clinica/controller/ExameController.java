package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Exame;
import projeto.aula.clinica.domain.Paciente;
import projeto.aula.clinica.dto.ResultadoExameRequest;
import projeto.aula.clinica.service.ExameService;

/**
 * API dos exames: CRUD padrão + registro de resultado e consulta do paciente.
 */
@RestController
@RequestMapping("/api/exames")
public class ExameController extends CrudController<Exame, ExameService> {

    public ExameController(ExameService service) {
        super(service);
    }

    /** Registra o resultado do exame e muda a situação para CONCLUIDO. */
    @PostMapping("/{id}/resultado")
    public Exame registrarResultado(@PathVariable int id, @RequestBody ResultadoExameRequest dados) {
        return service.registrarResultado(id, dados);
    }

    /** Paciente dono do exame, obtido pela consulta que o originou. */
    @GetMapping("/{id}/paciente")
    public Paciente paciente(@PathVariable int id) {
        return service.pacienteDoExame(id);
    }
}
