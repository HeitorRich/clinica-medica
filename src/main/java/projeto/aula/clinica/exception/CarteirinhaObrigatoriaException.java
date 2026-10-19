package projeto.aula.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Lançada ao vincular um convênio a um paciente sem informar o número da
 * carteirinha (cada paciente precisa ter a sua). Resposta HTTP 400.
 */
public class CarteirinhaObrigatoriaException extends RegraDeNegocioException {

    public CarteirinhaObrigatoriaException(String convenio) {
        super(HttpStatus.BAD_REQUEST, "Carteirinha obrigatória",
                "Informe o número da carteirinha do paciente"
                        + (convenio == null ? "" : " para o convênio " + convenio) + ".");
    }
}
