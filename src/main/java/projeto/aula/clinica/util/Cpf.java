package projeto.aula.clinica.util;

/**
 * Formatação de CPF em um único lugar (antes a regra vivia dentro do
 * {@code PacienteService}).
 */
public final class Cpf {

    private Cpf() {
        // classe utilitária: não deve ser instanciada
    }

    /**
     * Deixa o CPF no padrão {@code 000.000.000-00}, independentemente de o
     * cliente enviar {@code 12345678900} ou {@code 123.456.789-00}.
     * Valores com quantidade de dígitos diferente de 11 são mantidos como
     * recebidos (apenas sem espaços nas extremidades), pois podem ser dados
     * antigos já gravados.
     */
    public static String formatar(String cpf) {
        if (cpf == null) {
            return null;
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return cpf.trim();
        }
        return digitos.substring(0, 3) + "."
                + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-"
                + digitos.substring(9);
    }
}
