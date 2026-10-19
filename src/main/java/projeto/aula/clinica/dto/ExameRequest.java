package projeto.aula.clinica.dto;

/**
 * Corpo esperado em {@code POST /api/consultas/{id}/exames}.
 * A data da solicitação é a data da consulta e a situação inicial é SOLICITADO.
 */
public record ExameRequest(String nome, String tipo) {
}
