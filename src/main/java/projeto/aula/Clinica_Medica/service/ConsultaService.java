package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Consulta;
import projeto.aula.Clinica_Medica.Medico;
import projeto.aula.Clinica_Medica.Paciente;
import projeto.aula.Clinica_Medica.repository.ConsultaRepository;

@Service
public class ConsultaService extends AbstractCrudService<Consulta> {

    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    public ConsultaService(ConsultaRepository repository,
                           PacienteService pacienteService,
                           MedicoService medicoService) {
        super(repository);
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
    }

    @Override
    public Consulta salvar(Consulta consulta) {
        vincular(consulta);
        return super.salvar(consulta);
    }

    @Override
    protected void copiarCampos(Consulta origem, Consulta destino) {
        destino.data = origem.data;
        destino.horario = origem.horario;

        // revincula paciente/médico informados apenas pelo id
        if (origem.paciente != null && origem.paciente.id > 0) {
            Paciente novo = pacienteService.buscar(origem.paciente.id).orElse(null);
            if (novo != null) {
                destino.paciente = novo;
            }
        }
        if (origem.medico != null && origem.medico.id > 0) {
            Medico novo = medicoService.buscar(origem.medico.id).orElse(null);
            if (novo != null) {
                destino.medico = novo;
            }
        }
    }

    /**
     * Troca os objetos recebidos no JSON (que trazem apenas o id) pelos objetos
     * realmente cadastrados no banco, garantindo a chave estrangeira correta.
     */
    private void vincular(Consulta consulta) {
        if (consulta.paciente != null && consulta.paciente.id > 0) {
            consulta.paciente = pacienteService.buscar(consulta.paciente.id).orElse(null);
        }
        if (consulta.medico != null && consulta.medico.id > 0) {
            consulta.medico = medicoService.buscar(consulta.medico.id).orElse(null);
        }
    }
}
