package projeto.aula.clinica.domain;

/**
 * Situação de um exame solicitado na consulta.
 * Convertida para texto ("SOLICITADO"/"CONCLUIDO") no banco e no JSON.
 */
public enum SituacaoExame {

    /** Exame pedido pelo médico e ainda sem resultado. */
    SOLICITADO,

    /** Exame com resultado registrado. */
    CONCLUIDO
}
