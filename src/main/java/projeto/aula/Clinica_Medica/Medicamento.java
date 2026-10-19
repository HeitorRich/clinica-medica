package projeto.aula.Clinica_Medica;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicamento")
public class Medicamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String nome, dose, frequencia, duracao;

    public Medicamento() {
    }

    public Medicamento(int id, String nome, String dose, String frequencia, String duracao) {
        this.id = id; this.nome = nome; this.dose = dose; this.frequencia = frequencia; this.duracao = duracao;
    }
}
