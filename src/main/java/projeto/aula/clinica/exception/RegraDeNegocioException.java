package projeto.aula.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Falha esperada de regra de negócio (CPF repetido, carteirinha ausente, ...).
 *
 * A classe carrega o status HTTP e o título que devem aparecer na resposta;
 * o {@code ApiExceptionHandler} transforma isso em um ProblemDetail igual ao
 * que a API já devolvia antes. Assim o corpo do erro fica padronizado e as
 * subclasses só precisam descrever a mensagem para o usuário.
 */
public abstract class RegraDeNegocioException extends RuntimeException {

    private final HttpStatus status;
    private final String titulo;

    protected RegraDeNegocioException(HttpStatus status, String titulo, String mensagem) {
        super(mensagem);
        this.status = status;
        this.titulo = titulo;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getTitulo() {
        return titulo;
    }
}
