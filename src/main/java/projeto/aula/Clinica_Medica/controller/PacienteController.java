package projeto.aula.Clinica_Medica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import projeto.aula.Clinica_Medica.Consulta;
import projeto.aula.Clinica_Medica.Convenio;
import projeto.aula.Clinica_Medica.Paciente;
import projeto.aula.Clinica_Medica.PacienteConvenio;
import projeto.aula.Clinica_Medica.service.ConvenioService;
import projeto.aula.Clinica_Medica.service.PacienteService;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController extends AbstractCrudController<Paciente> {

    private final PacienteService pacienteService;
    private final ConvenioService convenioService;

    public PacienteController(PacienteService service, ConvenioService convenioService) {
        super(service);
        this.pacienteService = service;
        this.convenioService = convenioService;
    }

    /** Executa Paciente.consultarHistorico(). */
    @GetMapping("/{id}/historico")
    public List<Consulta> historico(@PathVariable int id) {
        return obrigatorio(id).consultarHistorico();
    }

    /** Executa Paciente.cadastrar(). */
    @PostMapping("/{id}/cadastrar")
    public Paciente cadastrar(@PathVariable int id) {
        Paciente paciente = obrigatorio(id);
        paciente.cadastrar();
        return service.salvar(paciente);
    }

    /** Executa Paciente.atualizarDados(nome, telefone). */
    @PutMapping("/{id}/dados")
    public Paciente atualizarDados(@PathVariable int id,
                                   @RequestParam String nome,
                                   @RequestParam String telefone) {
        Paciente paciente = obrigatorio(id);
        paciente.atualizarDados(nome, telefone);
        return service.salvar(paciente);
    }

    /** Convênios do paciente, cada um com o seu número de carteirinha. */
    @GetMapping("/{id}/convenios")
    public List<PacienteConvenio> convenios(@PathVariable int id) {
        return obrigatorio(id).convenios;
    }

    /**
     * Vincula uma operadora ao paciente guardando o número da carteirinha.
     * Se o vínculo já existir, atualiza a carteirinha/situação.
     */
    @PostMapping("/{id}/convenios/{convenioId}")
    public Paciente adicionarConvenio(@PathVariable int id,
                                      @PathVariable int convenioId,
                                      @RequestParam(required = false) String numeroCarteirinha,
                                      @RequestParam(required = false) String situacao) {
        return vincular(id, convenioId, numeroCarteirinha, situacao);
    }

    /** Atualiza a carteirinha/situação do vínculo com a operadora. */
    @PutMapping("/{id}/convenios/{convenioId}")
    public Paciente atualizarConvenio(@PathVariable int id,
                                      @PathVariable int convenioId,
                                      @RequestParam(required = false) String numeroCarteirinha,
                                      @RequestParam(required = false) String situacao) {
        return vincular(id, convenioId, numeroCarteirinha, situacao);
    }

    /** Desvincula a operadora do paciente. */
    @DeleteMapping("/{id}/convenios/{convenioId}")
    public Paciente removerConvenio(@PathVariable int id, @PathVariable int convenioId) {
        Paciente paciente = obrigatorio(id);
        return pacienteService.desvincularConvenio(paciente, convenioId);
    }

    /** Executa PacienteConvenio.validacaoCobertura() para o vínculo informado. */
    @GetMapping("/{id}/convenios/{convenioId}/cobertura")
    public boolean validarCobertura(@PathVariable int id, @PathVariable int convenioId) {
        return obrigatorio(id).buscarVinculo(convenioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paciente " + id + " não possui o convênio " + convenioId))
                .validacaoCobertura();
    }

    private Paciente vincular(int id, int convenioId, String numeroCarteirinha, String situacao) {
        Paciente paciente = obrigatorio(id);
        Convenio convenio = convenioService.buscar(convenioId)
                .orElseThrow(() -> naoEncontrado(convenioId));
        return pacienteService.vincularConvenio(paciente, convenio, numeroCarteirinha, situacao);
    }
}
