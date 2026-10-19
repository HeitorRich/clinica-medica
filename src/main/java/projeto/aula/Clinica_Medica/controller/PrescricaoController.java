package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.Medicamento;
import projeto.aula.Clinica_Medica.Prescricao;
import projeto.aula.Clinica_Medica.service.MedicamentoService;
import projeto.aula.Clinica_Medica.service.PrescricaoService;

@RestController
@RequestMapping("/api/prescricoes")
public class PrescricaoController extends AbstractCrudController<Prescricao> {

    private final MedicamentoService medicamentoService;

    public PrescricaoController(PrescricaoService service, MedicamentoService medicamentoService) {
        super(service);
        this.medicamentoService = medicamentoService;
    }

    /** Executa Prescricao.adicionarMedicamento(...). */
    @PostMapping("/{id}/medicamentos")
    public Prescricao adicionarMedicamento(@PathVariable int id, @RequestBody Medicamento medicamento) {
        Prescricao prescricao = obrigatorio(id);
        medicamentoService.salvar(medicamento);
        prescricao.adicionarMedicamento(medicamento);
        return service.salvar(prescricao);
    }
}
