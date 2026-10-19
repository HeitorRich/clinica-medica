package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Procedimento;
import projeto.aula.clinica.service.ProcedimentoService;

/**
 * API dos procedimentos: CRUD padrão + registro de realização.
 */
@RestController
@RequestMapping("/api/procedimentos")
public class ProcedimentoController extends CrudController<Procedimento, ProcedimentoService> {

    public ProcedimentoController(ProcedimentoService service) {
        super(service);
    }

    /** Registra a realização do procedimento. */
    @PostMapping("/{id}/realizar")
    public Procedimento realizar(@PathVariable int id) {
        return service.realizar(id);
    }
}
