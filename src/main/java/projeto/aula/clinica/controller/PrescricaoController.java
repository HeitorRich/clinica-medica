package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.Prescricao;
import projeto.aula.clinica.dto.MedicamentoRequest;
import projeto.aula.clinica.service.PrescricaoService;

/**
 * API das prescrições: CRUD padrão + inclusão de medicamentos.
 */
@RestController
@RequestMapping("/api/prescricoes")
public class PrescricaoController extends CrudController<Prescricao, PrescricaoService> {

    public PrescricaoController(PrescricaoService service) {
        super(service);
    }

    /** Adiciona um medicamento à receita. */
    @PostMapping("/{id}/medicamentos")
    public Prescricao adicionarMedicamento(@PathVariable int id, @RequestBody MedicamentoRequest dados) {
        return service.adicionarMedicamento(id, dados);
    }
}
