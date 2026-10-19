package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.ResultadoExame;
import projeto.aula.clinica.repository.ResultadoExameRepository;

/**
 * Resultados de exame (cadastro avulso; normalmente criados via
 * {@link ExameService#registrarResultado(int, projeto.aula.clinica.dto.ResultadoExameRequest)}).
 */
@Service
public class ResultadoExameService extends CrudService<ResultadoExame> {

    public ResultadoExameService(ResultadoExameRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(ResultadoExame origem, ResultadoExame destino) {
        destino.setData(origem.getData());
        destino.setDescricao(origem.getDescricao());
        destino.setConclusao(origem.getConclusao());
    }
}
