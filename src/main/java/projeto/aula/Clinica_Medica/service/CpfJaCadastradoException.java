package projeto.aula.Clinica_Medica.service;

/**
 * Lançada quando se tenta cadastrar (ou alterar) um paciente com um CPF que já
 * pertence a outro registro. O ApiExceptionHandler converte este erro em HTTP 409.
 */
public class CpfJaCadastradoException extends RuntimeException {

    public CpfJaCadastradoException(String cpf) {
        super("Já existe um paciente cadastrado com o CPF " + cpf + ".");
    }
}
