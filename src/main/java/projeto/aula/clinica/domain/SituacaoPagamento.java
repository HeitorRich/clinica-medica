package projeto.aula.clinica.domain;

/**
 * Situação de um pagamento ("PENDENTE"/"PAGO"/"ESTORNADO").
 * Guardada como texto, portanto os valores vistos pela API e pelo banco
 * continuam os mesmos de antes.
 */
public enum SituacaoPagamento {

    PENDENTE,
    PAGO,
    ESTORNADO
}
