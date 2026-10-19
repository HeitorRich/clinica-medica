package projeto.aula.clinica.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.aula.clinica.domain.ResultadoExame;
import projeto.aula.clinica.service.ResultadoExameService;

/**
 * API dos resultados de exame: apenas o CRUD padrão herdado de
 * {@link CrudController}.
 */
@RestController
@RequestMapping("/api/resultados-exame")
public class ResultadoExameController extends CrudController<ResultadoExame, ResultadoExameService> {

    public ResultadoExameController(ResultadoExameService service) {
        super(service);
    }
}
