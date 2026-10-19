package projeto.aula.clinica.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Medicamento de uma receita (posologia simples, sem catálogo próprio).
 */
@Entity
@Table(name = "medicamento")
@Getter
@Setter
@NoArgsConstructor
public class Medicamento extends EntidadeBase {

    private String nome;
    private String dose;
    private String frequencia;
    private String duracao;

    public Medicamento(String nome, String dose, String frequencia, String duracao) {
        this.nome = nome;
        this.dose = dose;
        this.frequencia = frequencia;
        this.duracao = duracao;
    }
}
