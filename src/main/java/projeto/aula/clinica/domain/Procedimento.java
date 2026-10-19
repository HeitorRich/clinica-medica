package projeto.aula.clinica.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Procedimento realizado em uma consulta, com o valor padrão e o valor
 * efetivamente cobrado do paciente.
 */
@Entity
@Table(name = "procedimento")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class Procedimento extends EntidadeBase {

    private String nome;
    private double valorPadrao;
    private double valorCobrado;

    public Procedimento(String nome, double valorPadrao, double valorCobrado) {
        this.nome = nome;
        this.valorPadrao = valorPadrao;
        this.valorCobrado = valorCobrado;
    }

    /** Usado pelo endpoint {@code POST /api/procedimentos/{id}/realizar}. */
    public void registrarRealizacao() {
        log.info("Procedimento realizado: {}", nome);
    }
}
