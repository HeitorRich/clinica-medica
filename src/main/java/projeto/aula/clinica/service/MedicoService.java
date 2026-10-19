package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Consulta;
import projeto.aula.clinica.domain.Especialidade;
import projeto.aula.clinica.domain.Medico;
import projeto.aula.clinica.repository.MedicoRepository;

import java.util.List;

/**
 * Cadastro de médicos, suas especialidades e agenda.
 */
@Service
public class MedicoService extends CrudService<Medico> {

    private final EspecialidadeService especialidadeService;

    public MedicoService(MedicoRepository repository, EspecialidadeService especialidadeService) {
        super(repository);
        this.especialidadeService = especialidadeService;
    }

    /** Na alteração, apenas nome e CRM mudam; as especialidades são geridas à parte. */
    @Override
    protected void copiarCampos(Medico origem, Medico destino) {
        destino.setNome(origem.getNome());
        destino.setCrm(origem.getCrm());
    }

    /** Consultas agendadas para o médico. */
    public List<Consulta> consultarAgenda(int medicoId) {
        return buscarObrigatorio(medicoId).consultarAgenda();
    }

    /** Vincula a especialidade (do catálogo) ao médico. */
    public Medico adicionarEspecialidade(int medicoId, int especialidadeId) {
        Medico medico = buscarObrigatorio(medicoId);
        Especialidade especialidade = especialidadeService.buscarObrigatorio(especialidadeId);
        medico.adicionarEspecialidade(especialidade);
        return salvar(medico);
    }

    /** Desvincula a especialidade do médico, sem apagar o cadastro dela. */
    public Medico removerEspecialidade(int medicoId, int especialidadeId) {
        Medico medico = buscarObrigatorio(medicoId);
        medico.removerEspecialidade(especialidadeId);
        return salvar(medico);
    }
}
