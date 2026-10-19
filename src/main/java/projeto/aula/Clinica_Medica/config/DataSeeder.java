package projeto.aula.Clinica_Medica.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import projeto.aula.Clinica_Medica.Consulta;
import projeto.aula.Clinica_Medica.Convenio;
import projeto.aula.Clinica_Medica.Especialidade;
import projeto.aula.Clinica_Medica.Exame;
import projeto.aula.Clinica_Medica.Medicamento;
import projeto.aula.Clinica_Medica.Medico;
import projeto.aula.Clinica_Medica.Paciente;
import projeto.aula.Clinica_Medica.Pagamento;
import projeto.aula.Clinica_Medica.Prescricao;
import projeto.aula.Clinica_Medica.Procedimento;
import projeto.aula.Clinica_Medica.ResultadoExame;
import projeto.aula.Clinica_Medica.service.ConsultaService;
import projeto.aula.Clinica_Medica.service.ConvenioService;
import projeto.aula.Clinica_Medica.service.EspecialidadeService;
import projeto.aula.Clinica_Medica.service.ExameService;
import projeto.aula.Clinica_Medica.service.MedicamentoService;
import projeto.aula.Clinica_Medica.service.MedicoService;
import projeto.aula.Clinica_Medica.service.PacienteService;
import projeto.aula.Clinica_Medica.service.PagamentoService;
import projeto.aula.Clinica_Medica.service.PrescricaoService;
import projeto.aula.Clinica_Medica.service.ProcedimentoService;
import projeto.aula.Clinica_Medica.service.ResultadoExameService;

/**
 * Popula o banco de dados com dados de exemplo na primeira inicialização,
 * reproduzindo o cenário do Main.java original. Se já houver dados, nada é feito
 * (evita duplicar registros a cada vez que a aplicação sobe).
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

        Especialidade cardiologia = especialidadeService.salvar(new Especialidade(0, "Cardiologia", "Coração"));
        especialidadeService.salvar(new Especialidade(0, "Pediatria", "Crianças"));

        Medico joao = medicoService.salvar(new Medico(0, "Dr. João", "CRM-12345"));
        joao.adicionarEspecialidade(cardiologia);

        // Catálogo de operadoras (cada convênio é cadastrado uma única vez)...
        Convenio unimed = convenioService.salvar(new Convenio(0, "58.160.789/0001-28", "Unimed", "304883"));
        Convenio bradesco = convenioService.salvar(new Convenio(0, "61.156.272/0001-58", "Bradesco Saúde", "005711"));

        Paciente maria = pacienteService.salvar(new Paciente(0, "123.456.789-00", "Maria", "20/05/1990", "99999-0000"));
        Paciente carlos = pacienteService.salvar(new Paciente(0, "987.654.321-00", "Carlos", "10/08/1985", "98888-1111"));

        // ...e vinculado aos pacientes com o número de carteirinha de cada um.
        maria = pacienteService.vincularConvenio(maria, unimed, "CARD-0001", "ATIVO");
        carlos = pacienteService.vincularConvenio(carlos, unimed, "CARD-0002", "ATIVO");
        pacienteService.vincularConvenio(carlos, bradesco, "BRAD-7788", "INATIVO");

        Consulta consulta = consultaService.salvar(new Consulta(0, "28/09/2026", "14:30", maria, joao));
        consulta.registrarAtend("Dor no peito", "Ansiedade", "Retorno em 30 dias");

        Exame exame = consulta.solicitarExa(0, "Eletrocardiograma", "Cardíaco");
        exameService.salvar(exame);
        ResultadoExame resultado = resultadoExameService.salvar(new ResultadoExame(0, "28/09/2026", "Normal", "Normal"));
        exame.registrarResultado(resultado);

        Prescricao prescricao = consulta.gerarPresc(0);
        prescricaoService.salvar(prescricao);
        prescricao.consulta = consulta;
        Medicamento medicamento = medicamentoService.salvar(new Medicamento(0, "Dipirona", "500mg", "8/8h", "5 dias"));
        prescricao.adicionarMedicamento(medicamento);

        Pagamento pagamento = new Pagamento(0, "28/09/2026", 250.0, "PIX");
        pagamento.registrarPag();
        pagamento.consulta = consulta;
        consulta.pagamentos.add(pagamento);
        pagamentoService.salvar(pagamento);

        Procedimento procedimento = procedimentoService.salvar(new Procedimento(0, "Eletrocardiograma", 150.0, 150.0));
        consulta.procedimentos.add(procedimento);
    }
}
