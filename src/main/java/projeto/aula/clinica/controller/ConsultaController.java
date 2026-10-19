package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Consulta;
import projeto.aula.clinica.domain.Exame;
import projeto.aula.clinica.domain.Pagamento;
import projeto.aula.clinica.domain.Prescricao;
import projeto.aula.clinica.domain.Procedimento;
import projeto.aula.clinica.dto.AtendimentoRequest;
import projeto.aula.clinica.dto.ExameRequest;
import projeto.aula.clinica.dto.PagamentoRequest;
import projeto.aula.clinica.dto.ProcedimentoRequest;
import projeto.aula.clinica.service.ConsultaService;

/**
 * API da consulta: CRUD padrão + os endpoints que compõem o atendimento
 * (registro do atendimento, exames, prescrição, procedimentos e pagamentos).
 *
 * O controller apenas traduz HTTP → chamada de serviço; as regras ficam em
 * {@link ConsultaService} e nas entidades de domínio.
 */
@RestController
@RequestMapping("/api/consultas")
public class ConsultaController extends CrudController<Consulta, ConsultaService> {

    public ConsultaController(ConsultaService service) {
        super(service);
    }

    /** Registra queixa principal, diagnóstico e observações. */
    @PostMapping("/{id}/atendimento")
    public Consulta registrarAtendimento(@PathVariable int id, @RequestBody AtendimentoRequest dados) {
        return service.registrarAtendimento(id, dados);
    }

    /** Solicita um exame para a consulta. */
    @PostMapping("/{id}/exames")
    public Exame solicitarExame(@PathVariable int id, @RequestBody ExameRequest dados) {
        return service.solicitarExame(id, dados);
    }

    /** Gera a prescrição da consulta. */
    @PostMapping("/{id}/prescricao")
    public Prescricao gerarPrescricao(@PathVariable int id) {
        return service.gerarPrescricao(id);
    }

    /** Adiciona um procedimento à consulta. */
    @PostMapping("/{id}/procedimentos")
    public Procedimento adicionarProcedimento(@PathVariable int id, @RequestBody ProcedimentoRequest dados) {
        return service.adicionarProcedimento(id, dados);
    }

    /** Lança um pagamento referente à consulta. */
    @PostMapping("/{id}/pagamentos")
    public Pagamento adicionarPagamento(@PathVariable int id, @RequestBody PagamentoRequest dados) {
        return service.adicionarPagamento(id, dados);
    }
}
