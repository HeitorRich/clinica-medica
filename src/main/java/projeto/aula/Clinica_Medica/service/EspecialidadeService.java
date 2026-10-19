package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Especialidade;
import projeto.aula.Clinica_Medica.repository.EspecialidadeRepository;

@Service
public class EspecialidadeService extends AbstractCrudService<Especialidade> {

    public EspecialidadeService(EspecialidadeRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Especialidade origem, Especialidade destino) {
        destino.nome = origem.nome;
        destino.descricao = origem.descricao;
    }
}
