package projeto.aula.Clinica_Medica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/hello")
public class HelloWordController {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String get(){
        return "Hello Word!";
    }
    @GetMapping(value="/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String get1(@PathVariable("id") String id){
        return "Hello Word" + id;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String post(){
        return "Hello Word!";
    }
}
