package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Consulta;
import projeto.aula.clinica.domain.Paciente;
import projeto.aula.clinica.domain.PacienteConvenio;
import projeto.aula.clinica.service.PacienteService;

import java.util.List;

/**
 * API do paciente: CRUD padrão + histórico de consultas e gestão de convênios.
 */
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController extends CrudController<Paciente, PacienteService> {

    public PacienteController(PacienteService service) {
        super(service);
    }

    /** Histórico de consultas do paciente. */
    @GetMapping("/{id}/historico")
    public List<Consulta> historico(@PathVariable int id) {
        return service.historico(id);
    }

    /** Executa a rotina de cadastro do paciente. */
    @PostMapping("/{id}/cadastrar")
    public Paciente cadastrar(@PathVariable int id) {
        return service.cadastrar(id);
    }

    /** Atualiza somente nome e telefone do paciente. */
    @PutMapping("/{id}/dados")
    public Paciente atualizarDados(@PathVariable int id,
                                   @RequestParam String nome,
                                   @RequestParam String telefone) {
        return service.atualizarDados(id, nome, telefone);
    }

    /** Convênios do paciente, cada um com o seu número de carteirinha. */
    @GetMapping("/{id}/convenios")
    public List<PacienteConvenio> convenios(@PathVariable int id) {
        return service.listarConvenios(id);
    }

    /** Vincula a operadora ao paciente; se o vínculo já existir, atualiza os dados. */
    @PostMapping("/{id}/convenios/{convenioId}")
    public Paciente adicionarConvenio(@PathVariable int id,
                                      @PathVariable int convenioId,
                                      @RequestParam(required = false) String numeroCarteirinha,
                                      @RequestParam(required = false) String situacao) {
        return service.vincularConvenio(id, convenioId, numeroCarteirinha, situacao);
    }

    /** Atualiza a carteirinha/situação do vínculo com a operadora. */
    @PutMapping("/{id}/convenios/{convenioId}")
    public Paciente atualizarConvenio(@PathVariable int id,
                                      @PathVariable int convenioId,
                                      @RequestParam(required = false) String numeroCarteirinha,
                                      @RequestParam(required = false) String situacao) {
        return service.vincularConvenio(id, convenioId, numeroCarteirinha, situacao);
    }

    /** Desvincula a operadora do paciente. */
    @DeleteMapping("/{id}/convenios/{convenioId}")
    public Paciente removerConvenio(@PathVariable int id, @PathVariable int convenioId) {
        return service.desvincularConvenio(id, convenioId);
    }

    /** Informa se o vínculo informado está com a cobertura ativa. */
    @GetMapping("/{id}/convenios/{convenioId}/cobertura")
    public boolean validarCobertura(@PathVariable int id, @PathVariable int convenioId) {
        return service.validarCobertura(id, convenioId);
    }
}
