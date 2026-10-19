package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Pagamento;
import projeto.aula.Clinica_Medica.repository.PagamentoRepository;

@Service
public class PagamentoService extends AbstractCrudService<Pagamento> {

    public PagamentoService(PagamentoRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Pagamento origem, Pagamento destino) {
        destino.data = origem.data;
        destino.valor = origem.valor;
        destino.formaPagamento = origem.formaPagamento;
        destino.consulta = origem.consulta;
    }
}
