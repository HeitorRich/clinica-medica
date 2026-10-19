package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.Procedimento;
import projeto.aula.Clinica_Medica.service.ProcedimentoService;

@RestController
@RequestMapping("/api/procedimentos")
public class ProcedimentoController extends AbstractCrudController<Procedimento> {

    public ProcedimentoController(ProcedimentoService service) {
        super(service);
    }

    /** Executa Procedimento.registrarRealizacao(). */
    @PostMapping("/{id}/realizar")
    public Procedimento realizar(@PathVariable int id) {
        Procedimento procedimento = obrigatorio(id);
        procedimento.registrarRealizacao();
        return service.salvar(procedimento);
    }
}
