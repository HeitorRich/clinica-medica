package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.Consulta;
import projeto.aula.Clinica_Medica.Exame;
import projeto.aula.Clinica_Medica.Pagamento;
import projeto.aula.Clinica_Medica.Prescricao;
import projeto.aula.Clinica_Medica.Procedimento;
import projeto.aula.Clinica_Medica.service.ConsultaService;
import projeto.aula.Clinica_Medica.service.ExameService;
import projeto.aula.Clinica_Medica.service.PagamentoService;
import projeto.aula.Clinica_Medica.service.PrescricaoService;
import projeto.aula.Clinica_Medica.service.ProcedimentoService;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController extends AbstractCrudController<Consulta> {

    private final ExameService exameService;
    private final PrescricaoService prescricaoService;
    private final ProcedimentoService procedimentoService;
    private final PagamentoService pagamentoService;

    public ConsultaController(ConsultaService service,
                              ExameService exameService,
                              PrescricaoService prescricaoService,
                              ProcedimentoService procedimentoService,
                              PagamentoService pagamentoService) {
        super(service);
        this.exameService = exameService;
        this.prescricaoService = prescricaoService;
        this.procedimentoService = procedimentoService;
        this.pagamentoService = pagamentoService;
    }

    /** Executa Consulta.registrarAtend(queixa, diagnostico, observacoes). */
    @PostMapping("/{id}/atendimento")
    public Consulta registrarAtendimento(@PathVariable int id, @RequestBody Consulta dados) {
        Consulta consulta = obrigatorio(id);
        consulta.registrarAtend(dados.queixaPrincipal, dados.diagnostico, dados.observacoes);
        return service.salvar(consulta);
    }

    /** Executa Consulta.solicitarExa(...) e guarda o exame no catálogo de exames. */
    @PostMapping("/{id}/exames")
    public Exame solicitarExame(@PathVariable int id, @RequestBody Exame exame) {
        Consulta consulta = obrigatorio(id);
        exame.dataSolicitacao = consulta.data;
        exame.consulta = consulta;
        consulta.exames.add(exame);
        return exameService.salvar(exame);
    }

    /** Executa Consulta.gerarPresc(...). */
    @PostMapping("/{id}/prescricao")
    public Prescricao gerarPrescricao(@PathVariable int id) {
        Consulta consulta = obrigatorio(id);
        Prescricao prescricao = prescricaoService.salvar(new Prescricao(0, consulta.data));
        prescricao.consulta = consulta;
        consulta.prescricao = prescricao;
        service.salvar(consulta);
        return prescricao;
    }

    /** Adiciona um procedimento à consulta. */
    @PostMapping("/{id}/procedimentos")
    public Procedimento adicionarProcedimento(@PathVariable int id, @RequestBody Procedimento procedimento) {
        Consulta consulta = obrigatorio(id);
        procedimentoService.salvar(procedimento);
        consulta.procedimentos.add(procedimento);
        service.salvar(consulta);
        return procedimento;
    }

    /** Adiciona um pagamento à consulta. */
    @PostMapping("/{id}/pagamentos")
    public Pagamento adicionarPagamento(@PathVariable int id, @RequestBody Pagamento pagamento) {
        Consulta consulta = obrigatorio(id);
        pagamento.consulta = consulta;
        consulta.pagamentos.add(pagamento);
        return pagamentoService.salvar(pagamento);
    }
}
