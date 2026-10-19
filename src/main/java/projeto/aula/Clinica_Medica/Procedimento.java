package projeto.aula.Clinica_Medica;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "procedimento")
public class Procedimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String nome;
    public double valorPadrao, valorCobrado;

    public Procedimento() {
    }

    public Procedimento(int id, String nome, double valorPadrao, double valorCobrado) {
        this.id = id; this.nome = nome; this.valorPadrao = valorPadrao; this.valorCobrado = valorCobrado;
    }

    public void registrarRealizacao() { System.out.println("Procedimento realizado: " + nome); }
}
