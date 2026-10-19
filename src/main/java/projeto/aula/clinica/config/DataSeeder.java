package projeto.aula.clinica.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import projeto.aula.clinica.domain.Consulta;
import projeto.aula.clinica.domain.Convenio;
import projeto.aula.clinica.domain.Especialidade;
import projeto.aula.clinica.domain.Exame;
import projeto.aula.clinica.domain.Medicamento;
import projeto.aula.clinica.domain.Medico;
import projeto.aula.clinica.domain.Paciente;
import projeto.aula.clinica.domain.Pagamento;
import projeto.aula.clinica.domain.Prescricao;
import projeto.aula.clinica.domain.Procedimento;
import projeto.aula.clinica.domain.ResultadoExame;
import projeto.aula.clinica.domain.SituacaoCobertura;
import projeto.aula.clinica.service.ConsultaService;
import projeto.aula.clinica.service.ConvenioService;
import projeto.aula.clinica.service.EspecialidadeService;
import projeto.aula.clinica.service.ExameService;
import projeto.aula.clinica.service.MedicamentoService;
import projeto.aula.clinica.service.MedicoService;
import projeto.aula.clinica.service.PacienteService;
import projeto.aula.clinica.service.PagamentoService;
import projeto.aula.clinica.service.PrescricaoService;
import projeto.aula.clinica.service.ProcedimentoService;
import projeto.aula.clinica.service.ResultadoExameService;

/**
 * Popula o banco com um cenário de exemplo na primeira inicialização,
 * reproduzindo os dados do sistema original. Se já houver pacientes
 * cadastrados, nada é feito - assim o seeder não duplica registros a cada vez
 * que a aplicação sobe.
 *
 * O método roda dentro de uma transação: as entidades carregadas ficam
 * gerenciadas pelo Hibernate e tudo o que for alterado nelas é gravado no
 * commit (prescrição da consulta, lista de procedimentos, etc.), sem precisar
 * de um save explícito para cada mudança.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private final EspecialidadeService especialidadeService;
    private final MedicoService medicoService;
    private final PacienteService pacienteService;
    private final ConvenioService convenioService;
    private final ConsultaService consultaService;
    private final ExameService exameService;
    private final ResultadoExameService resultadoExameService;
    private final PrescricaoService prescricaoService;
    private final MedicamentoService medicamentoService;
    private final PagamentoService pagamentoService;
    private final ProcedimentoService procedimentoService;

    public DataSeeder(EspecialidadeService especialidadeService,
                      MedicoService medicoService,
                      PacienteService pacienteService,
                      ConvenioService convenioService,
                      ConsultaService consultaService,
                      ExameService exameService,
                      ResultadoExameService resultadoExameService,
                      PrescricaoService prescricaoService,
                      MedicamentoService medicamentoService,
                      PagamentoService pagamentoService,
                      ProcedimentoService procedimentoService) {
        this.especialidadeService = especialidadeService;
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
        this.convenioService = convenioService;
        this.consultaService = consultaService;
        this.exameService = exameService;
        this.resultadoExameService = resultadoExameService;
        this.prescricaoService = prescricaoService;
        this.medicamentoService = medicamentoService;
        this.pagamentoService = pagamentoService;
        this.procedimentoService = procedimentoService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!pacienteService.listar().isEmpty()) {
            return;
        }

        Especialidade cardiologia = especialidadeService.salvar(new Especialidade("Cardiologia", "Coração"));
        especialidadeService.salvar(new Especialidade("Pediatria", "Crianças"));

        Medico medico = medicoService.salvar(new Medico("Dr. João", "CRM-12345"));
        medico.adicionarEspecialidade(cardiologia);

        criarPacientesEConsulta(medico);
    }

    /** Pacientes, convênios e uma consulta completa (atendimento, exame, receita e pagamento). */
    private void criarPacientesEConsulta(Medico medico) {
        // Catálogo de operadoras: cada convênio é cadastrado uma única vez...
        Convenio unimed = convenioService.salvar(new Convenio("58.160.789/0001-28", "Unimed", "304883"));
        Convenio bradesco = convenioService.salvar(new Convenio("61.156.272/0001-58", "Bradesco Saúde", "005711"));

        Paciente maria = pacienteService.salvar(new Paciente("123.456.789-00", "Maria", "20/05/1990", "99999-0000"));
        Paciente carlos = pacienteService.salvar(new Paciente("987.654.321-00", "Carlos", "10/08/1985", "98888-1111"));

        // ...e vinculado aos pacientes com o número de carteirinha de cada um.
        pacienteService.vincularConvenio(maria.getId(), unimed.getId(), "CARD-0001", SituacaoCobertura.ATIVO.name());
        pacienteService.vincularConvenio(carlos.getId(), unimed.getId(), "CARD-0002", SituacaoCobertura.ATIVO.name());
        pacienteService.vincularConvenio(carlos.getId(), bradesco.getId(), "BRAD-7788", SituacaoCobertura.INATIVO.name());

        Consulta consulta = consultaService.salvar(new Consulta("28/09/2026", "14:30", maria, medico));
        consulta.registrarAtendimento("Dor no peito", "Ansiedade", "Retorno em 30 dias");

        Exame exame = consulta.solicitarExame("Eletrocardiograma", "Cardíaco");
        exameService.salvar(exame);
        ResultadoExame resultado = resultadoExameService.salvar(new ResultadoExame("28/09/2026", "Normal", "Normal"));
        exame.registrarResultado(resultado);

        Prescricao prescricao = consulta.gerarPrescricao();
        prescricaoService.salvar(prescricao);
        Medicamento medicamento = medicamentoService.salvar(new Medicamento("Dipirona", "500mg", "8/8h", "5 dias"));
        prescricao.adicionarMedicamento(medicamento);

        Pagamento pagamento = new Pagamento("28/09/2026", 250.0, "PIX");
        pagamento.registrarPagamento();
        consulta.adicionarPagamento(pagamento);
        pagamentoService.salvar(pagamento);

        Procedimento procedimento = procedimentoService.salvar(new Procedimento("Eletrocardiograma", 150.0, 150.0));
        consulta.adicionarProcedimento(procedimento);
    }
}
