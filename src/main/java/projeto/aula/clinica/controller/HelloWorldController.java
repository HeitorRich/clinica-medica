package projeto.aula.clinica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de exemplo, usado para checar rapidamente se a aplicação subiu.
 * As rotas continuam as mesmas de antes: {@code /v1/hello}.
 */
@RestController
@RequestMapping("/v1/hello")
public class HelloWorldController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String get() {
        return "Hello Word!";
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getPorId(@PathVariable("id") String id) {
        return "Hello Word" + id;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String post() {
        return "Hello Word!";
    }
}
