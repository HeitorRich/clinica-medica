package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Medicamento;
import projeto.aula.clinica.service.MedicamentoService;

/**
 * API dos medicamentos: apenas o CRUD padrão herdado de
 * {@link CrudController}.
 */
@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController extends CrudController<Medicamento, MedicamentoService> {

    public MedicamentoController(MedicamentoService service) {
        super(service);
    }
}
