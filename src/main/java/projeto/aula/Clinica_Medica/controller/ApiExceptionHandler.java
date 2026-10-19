package projeto.aula.Clinica_Medica.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import projeto.aula.Clinica_Medica.service.CarteirinhaObrigatoriaException;
import projeto.aula.Clinica_Medica.service.CpfJaCadastradoException;

/**
 * Converte violações de integridade referencial do PostgreSQL (por exemplo,
 * excluir um paciente que ainda possui consultas) em uma resposta 409 amigável,
 * em vez de um erro 500 genérico.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridade(DataIntegrityViolationException ex) {
        ProblemDetail detalhe = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detalhe.setTitle("Vínculo existente");
        detalhe.setDetail("Não foi possível concluir a operação: o registro possui vínculos com outros registros.");
        return detalhe;
    }

    /** CPF repetido: regra de negócio validada na camada de serviço. */
    @ExceptionHandler(CpfJaCadastradoException.class)
    public ProblemDetail cpfJaCadastrado(CpfJaCadastradoException ex) {
        ProblemDetail detalhe = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detalhe.setTitle("CPF já cadastrado");
        detalhe.setDetail(ex.getMessage());
        return detalhe;
    }

    /** Vínculo paciente x convênio sem o número de carteirinha. */
    @ExceptionHandler(CarteirinhaObrigatoriaException.class)
    public ProblemDetail carteirinhaObrigatoria(CarteirinhaObrigatoriaException ex) {
        ProblemDetail detalhe = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detalhe.setTitle("Carteirinha obrigatória");
        detalhe.setDetail(ex.getMessage());
        return detalhe;
    }
}
