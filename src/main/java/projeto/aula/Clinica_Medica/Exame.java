package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "exame")
public class Exame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String nome, tipo, dataSolicitacao, situacao = "SOLICITADO";

    /**
     * Consulta que originou o exame. É por ela que se sabe a qual paciente o
     * exame pertence (consulta.paciente). As listas da consulta são omitidas
     * na serialização para não gerar referência circular
     * (consulta -> exames -> consulta -> ...).
     */
    @ManyToOne
    @JoinColumn(name = "consulta_id")
    @JsonIgnoreProperties({"exames", "procedimentos", "pagamentos", "prescricao"})
    public Consulta consulta;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resultado_id")
    @JsonIgnoreProperties({"exame"})
    public ResultadoExame resultado;

    public Exame() {
    }

    public Exame(int id, String nome, String tipo, String dataSolicitacao) {
        this.id = id; this.nome = nome; this.tipo = tipo; this.dataSolicitacao = dataSolicitacao;
    }

    public void registrarResultado(ResultadoExame r) { resultado = r; r.exame = this; situacao = "CONCLUIDO"; }

    /** Paciente dono do exame, obtido pela consulta que o originou. */
    public Paciente consultarPaciente() {
        return consulta == null ? null : consulta.paciente;
    }
}
