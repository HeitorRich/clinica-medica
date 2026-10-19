package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Especialidade;
import projeto.aula.clinica.domain.Medico;
import projeto.aula.clinica.repository.EspecialidadeRepository;

import java.util.List;

/**
 * Catálogo de especialidades médicas.
 */
@Service
public class EspecialidadeService extends CrudService<Especialidade> {

    public EspecialidadeService(EspecialidadeRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Especialidade origem, Especialidade destino) {
        destino.setNome(origem.getNome());
        destino.setDescricao(origem.getDescricao());
    }

    /** Médicos que possuem esta especialidade. */
    public List<Medico> listarMedicos(int especialidadeId) {
        return buscarObrigatorio(especialidadeId).listaDeMedicos();
    }
}
