package projeto.aula.clinica.domain;

/**
 * Situação da cobertura de um paciente em uma operadora
 * (campo {@code situacao} de {@link PacienteConvenio}).
 */
public enum SituacaoCobertura {

    ATIVO,
    INATIVO;

    /**
     * Converte o texto recebido do cliente (query string) para o enum.
     * Valores vazios ou desconhecidos retornam {@code null} para que o vínculo
     * mantenha a situação que já estava gravada - mesma tolerância de antes.
     */
    public static SituacaoCobertura deTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
