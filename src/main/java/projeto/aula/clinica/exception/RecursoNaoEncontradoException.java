package projeto.aula.clinica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Registro (ou vínculo) inexistente → resposta HTTP 404.
 *
 * Estende {@link ResponseStatusException} de propósito: o Spring já sabe montar
 * o corpo de erro (ProblemDetail com title/detail) para essa exceção, então a
 * API mantém exatamente o mesmo formato de resposta de antes.
 */
public class RecursoNaoEncontradoException extends ResponseStatusException {

    private RecursoNaoEncontradoException(String mensagem) {
        super(HttpStatus.NOT_FOUND, mensagem);
    }

    /** Registro com o id informado não existe. */
    public static RecursoNaoEncontradoException paraRegistro(int id) {
        return new RecursoNaoEncontradoException("Registro " + id + " não encontrado");
    }

    /** O paciente existe, mas não possui vínculo com a operadora informada. */
    public static RecursoNaoEncontradoException paraVinculo(int pacienteId, int convenioId) {
        return new RecursoNaoEncontradoException(
                "Paciente " + pacienteId + " não possui o convênio " + convenioId);
    }
}
