package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.ResultadoExame;
import projeto.aula.Clinica_Medica.repository.ResultadoExameRepository;

@Service
public class ResultadoExameService extends AbstractCrudService<ResultadoExame> {

    public ResultadoExameService(ResultadoExameRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(ResultadoExame origem, ResultadoExame destino) {
        destino.data = origem.data;
        destino.descricao = origem.descricao;
        destino.conclusao = origem.conclusao;
    }
}
