package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Pagamento;
import projeto.aula.clinica.repository.PagamentoRepository;

/**
 * Pagamentos das consultas (lançamento, confirmação e estorno).
 */
@Service
public class PagamentoService extends CrudService<Pagamento> {

    public PagamentoService(PagamentoRepository repository) {
        super(repository);
    }

    /**
     * A situação é alterada apenas pelos endpoints /pagar e /estornar, por isso
     * aqui ela não é copiada de uma atualização comum.
     */
    @Override
    protected void copiarCampos(Pagamento origem, Pagamento destino) {
        destino.setData(origem.getData());
        destino.setValor(origem.getValor());
        destino.setFormaPagamento(origem.getFormaPagamento());
        destino.setConsulta(origem.getConsulta());
    }

    /** Confirma o pagamento (situação PAGO). */
    public Pagamento pagar(int pagamentoId) {
        Pagamento pagamento = buscarObrigatorio(pagamentoId);
        pagamento.registrarPagamento();
        return salvar(pagamento);
    }

    /** Estorna o pagamento (situação ESTORNADO). */
    public Pagamento estornar(int pagamentoId) {
        Pagamento pagamento = buscarObrigatorio(pagamentoId);
        pagamento.estornar();
        return salvar(pagamento);
    }
}
