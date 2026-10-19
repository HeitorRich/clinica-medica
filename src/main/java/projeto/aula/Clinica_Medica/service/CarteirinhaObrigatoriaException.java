package projeto.aula.Clinica_Medica.service;

/**
 * Lançada quando se tenta vincular um convênio a um paciente sem informar o
 * número da carteirinha (cada paciente precisa ter a sua).
 * O ApiExceptionHandler converte este erro em HTTP 400.
 */
public class CarteirinhaObrigatoriaException extends RuntimeException {

    public CarteirinhaObrigatoriaException(String convenio) {
        super("Informe o número da carteirinha do paciente"
                + (convenio == null ? "" : " para o convênio " + convenio) + ".");
    }
}
