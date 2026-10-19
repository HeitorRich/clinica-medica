package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Consulta;
import projeto.aula.clinica.domain.Exame;
import projeto.aula.clinica.domain.Medico;
import projeto.aula.clinica.domain.Paciente;
import projeto.aula.clinica.domain.Pagamento;
import projeto.aula.clinica.domain.Prescricao;
import projeto.aula.clinica.domain.Procedimento;
import projeto.aula.clinica.dto.AtendimentoRequest;
import projeto.aula.clinica.dto.ExameRequest;
import projeto.aula.clinica.dto.PagamentoRequest;
import projeto.aula.clinica.dto.ProcedimentoRequest;
import projeto.aula.clinica.repository.ConsultaRepository;

/**
 * Regras da consulta, que é o agregado central do sistema: atendimento,
 * exames, procedimentos, prescrição e pagamentos giram em torno dela.
 */
@Service
public class ConsultaService extends CrudService<Consulta> {

    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final ExameService exameService;
    private final PrescricaoService prescricaoService;
    private final ProcedimentoService procedimentoService;
    private final PagamentoService pagamentoService;

    public ConsultaService(ConsultaRepository repository,
                           PacienteService pacienteService,
                           MedicoService medicoService,
                           ExameService exameService,
                           PrescricaoService prescricaoService,
                           ProcedimentoService procedimentoService,
                           PagamentoService pagamentoService) {
        super(repository);
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.exameService = exameService;
        this.prescricaoService = prescricaoService;
        this.procedimentoService = procedimentoService;
        this.pagamentoService = pagamentoService;
    }

    @Override
    public Consulta salvar(Consulta consulta) {
        consulta.setPaciente(pacienteDoBanco(consulta.getPaciente()));
        consulta.setMedico(medicoDoBanco(consulta.getMedico()));
        return super.salvar(consulta);
    }

    /** Na edição só data, horário e vínculos mudam; o atendimento tem endpoint próprio. */
    @Override
    protected void copiarCampos(Consulta origem, Consulta destino) {
        destino.setData(origem.getData());
        destino.setHorario(origem.getHorario());
        revincularPaciente(origem.getPaciente(), destino);
        revincularMedico(origem.getMedico(), destino);
    }

    /** Registra queixa, diagnóstico e observações do atendimento. */
    public Consulta registrarAtendimento(int consultaId, AtendimentoRequest dados) {
        Consulta consulta = buscarObrigatorio(consultaId);
        consulta.registrarAtendimento(dados.queixaPrincipal(), dados.diagnostico(), dados.observacoes());
        return salvar(consulta);
    }

    /** Solicita um novo exame na consulta (criado com a data da consulta). */
    public Exame solicitarExame(int consultaId, ExameRequest dados) {
        Consulta consulta = buscarObrigatorio(consultaId);
        return exameService.salvar(consulta.solicitarExame(dados.nome(), dados.tipo()));
    }

    /** Gera (uma única vez) a prescrição vinculada à consulta. */
    public Prescricao gerarPrescricao(int consultaId) {
        Consulta consulta = buscarObrigatorio(consultaId);
        Prescricao prescricao = consulta.gerarPrescricao();
        prescricaoService.salvar(prescricao);
        salvar(consulta);
        return prescricao;
    }

    /** Adiciona um procedimento realizado durante a consulta. */
    public Procedimento adicionarProcedimento(int consultaId, ProcedimentoRequest dados) {
        Consulta consulta = buscarObrigatorio(consultaId);
        Procedimento procedimento = procedimentoService.salvar(
                new Procedimento(dados.nome(), dados.valorPadrao(), dados.valorCobrado()));
        consulta.adicionarProcedimento(procedimento);
        salvar(consulta);
        return procedimento;
    }

    /** Lança um pagamento (PENDENTE) referente à consulta. */
    public Pagamento adicionarPagamento(int consultaId, PagamentoRequest dados) {
        Consulta consulta = buscarObrigatorio(consultaId);
        Pagamento pagamento = new Pagamento(dados.data(), dados.valor(), dados.formaPagamento());
        consulta.adicionarPagamento(pagamento);
        return pagamentoService.salvar(pagamento);
    }

    /**
     * Troca o objeto recebido no JSON (que traz apenas o id) pelo registro do
     * banco, garantindo a chave estrangeira correta. Se o id não existir, o
     * vínculo fica nulo - mesma regra usada antes nas criações.
     */
    private Paciente pacienteDoBanco(Paciente informado) {
        if (informado == null || informado.getId() <= 0) {
            return informado;
        }
        return pacienteService.buscar(informado.getId()).orElse(null);
    }

    private Medico medicoDoBanco(Medico informado) {
        if (informado == null || informado.getId() <= 0) {
            return informado;
        }
        return medicoService.buscar(informado.getId()).orElse(null);
    }

    /** Na edição, um id inexistente mantém o vínculo atual (não zera o paciente). */
    private void revincularPaciente(Paciente informado, Consulta destino) {
        if (informado != null && informado.getId() > 0) {
            pacienteService.buscar(informado.getId()).ifPresent(destino::setPaciente);
        }
    }

    private void revincularMedico(Medico informado, Consulta destino) {
        if (informado != null && informado.getId() > 0) {
            medicoService.buscar(informado.getId()).ifPresent(destino::setMedico);
        }
    }
}
