package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Procedimento;
import projeto.aula.clinica.repository.ProcedimentoRepository;

/**
 * Procedimentos realizados nas consultas.
 */
@Service
public class ProcedimentoService extends CrudService<Procedimento> {

    public ProcedimentoService(ProcedimentoRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Procedimento origem, Procedimento destino) {
        destino.setNome(origem.getNome());
        destino.setValorPadrao(origem.getValorPadrao());
        destino.setValorCobrado(origem.getValorCobrado());
    }

    /** Registra a realização do procedimento (registro em log). */
    public Procedimento realizar(int procedimentoId) {
        Procedimento procedimento = buscarObrigatorio(procedimentoId);
        procedimento.registrarRealizacao();
        return salvar(procedimento);
    }
}
