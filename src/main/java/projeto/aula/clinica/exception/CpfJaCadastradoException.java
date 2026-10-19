package projeto.aula.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Lançada ao cadastrar (ou alterar) um paciente com CPF já usado por outro
 * registro. Resposta HTTP 409 (Conflict).
 */
public class CpfJaCadastradoException extends RegraDeNegocioException {

    public CpfJaCadastradoException(String cpf) {
        super(HttpStatus.CONFLICT, "CPF já cadastrado",
                "Já existe um paciente cadastrado com o CPF " + cpf + ".");
    }
}
