package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Exame;
import projeto.aula.clinica.domain.Paciente;
import projeto.aula.clinica.domain.ResultadoExame;
import projeto.aula.clinica.dto.ResultadoExameRequest;
import projeto.aula.clinica.repository.ExameRepository;

/**
 * Exames solicitados e seus resultados.
 */
@Service
public class ExameService extends CrudService<Exame> {

    private final ResultadoExameService resultadoExameService;

    public ExameService(ExameRepository repository, ResultadoExameService resultadoExameService) {
        super(repository);
        this.resultadoExameService = resultadoExameService;
    }

    /** A situação do exame é controlada pelo sistema (SOLICITADO → CONCLUIDO). */
    @Override
    protected void copiarCampos(Exame origem, Exame destino) {
        destino.setNome(origem.getNome());
        destino.setTipo(origem.getTipo());
        destino.setDataSolicitacao(origem.getDataSolicitacao());
        destino.setConsulta(origem.getConsulta());
    }

    /**
     * Grava o resultado do exame e o marca como CONCLUIDO.
     * O exame é localizado antes para que um id inexistente devolva 404.
     */
    public Exame registrarResultado(int exameId, ResultadoExameRequest dados) {
        Exame exame = buscarObrigatorio(exameId);
        ResultadoExame resultado = resultadoExameService.salvar(
                new ResultadoExame(dados.data(), dados.descricao(), dados.conclusao()));
        exame.registrarResultado(resultado);
        return salvar(exame);
    }

    /** Paciente dono do exame, obtido pela consulta que o originou. */
    public Paciente pacienteDoExame(int exameId) {
        return buscarObrigatorio(exameId).pacienteDoExame();
    }
}
