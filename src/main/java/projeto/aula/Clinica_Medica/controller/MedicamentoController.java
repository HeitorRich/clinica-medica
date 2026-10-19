package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.Medicamento;
import projeto.aula.Clinica_Medica.service.MedicamentoService;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController extends AbstractCrudController<Medicamento> {

    public MedicamentoController(MedicamentoService service) {
        super(service);
    }
}
