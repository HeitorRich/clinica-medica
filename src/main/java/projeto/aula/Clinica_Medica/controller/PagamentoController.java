package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.Pagamento;
import projeto.aula.Clinica_Medica.service.PagamentoService;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController extends AbstractCrudController<Pagamento> {

    public PagamentoController(PagamentoService service) {
        super(service);
    }

    /** Executa Pagamento.registrarPag(). */
    @PostMapping("/{id}/pagar")
    public Pagamento pagar(@PathVariable int id) {
        Pagamento pagamento = obrigatorio(id);
        pagamento.registrarPag();
        return service.salvar(pagamento);
    }

    /** Executa Pagamento.estornarPag(). */
    @PostMapping("/{id}/estornar")
    public Pagamento estornar(@PathVariable int id) {
        Pagamento pagamento = obrigatorio(id);
        pagamento.estornarPag();
        return service.salvar(pagamento);
    }
}
