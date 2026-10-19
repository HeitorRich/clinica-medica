package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Pagamento;
import projeto.aula.clinica.service.PagamentoService;

/**
 * API dos pagamentos: CRUD padrão + confirmação e estorno.
 */
@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController extends CrudController<Pagamento, PagamentoService> {

    public PagamentoController(PagamentoService service) {
        super(service);
    }

    /** Confirma o pagamento (situação PAGO). */
    @PostMapping("/{id}/pagar")
    public Pagamento pagar(@PathVariable int id) {
        return service.pagar(id);
    }

    /** Estorna o pagamento (situação ESTORNADO). */
    @PostMapping("/{id}/estornar")
    public Pagamento estornar(@PathVariable int id) {
        return service.estornar(id);
    }
}
