package projeto.aula.clinica.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import projeto.aula.clinica.exception.RegraDeNegocioException;

/**
 * Converte exceções em respostas de erro padronizadas (ProblemDetail).
 *
 * Fica separado dos controllers de propósito: eles cuidam apenas do fluxo
 * normal e as falhas são tratadas em um único lugar.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Violação de integridade referencial no PostgreSQL (por exemplo, excluir um
     * paciente que ainda possui consultas) → 409 em vez de um 500 genérico.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail vinculoExistente(DataIntegrityViolationException ex) {
        ProblemDetail detalhe = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detalhe.setTitle("Vínculo existente");
        detalhe.setDetail("Não foi possível concluir a operação: o registro possui vínculos com outros registros.");
        return detalhe;
    }

    /** Regras de negócio (CPF repetido, carteirinha ausente, ...). */
    @ExceptionHandler(RegraDeNegocioException.class)
    public ProblemDetail regraDeNegocio(RegraDeNegocioException ex) {
        ProblemDetail detalhe = ProblemDetail.forStatus(ex.getStatus());
        detalhe.setTitle(ex.getTitulo());
        detalhe.setDetail(ex.getMessage());
        return detalhe;
    }
}
