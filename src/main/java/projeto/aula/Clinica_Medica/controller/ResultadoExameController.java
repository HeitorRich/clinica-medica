package projeto.aula.Clinica_Medica.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.Clinica_Medica.ResultadoExame;
import projeto.aula.Clinica_Medica.service.ResultadoExameService;

@RestController
@RequestMapping("/api/resultados-exame")
public class ResultadoExameController extends AbstractCrudController<ResultadoExame> {

    public ResultadoExameController(ResultadoExameService service) {
        super(service);
    }
}
