package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Consulta agendada entre um paciente e um médico.
 *
 * É o "agregado" central do sistema: a partir dela são registrados o
 * atendimento, os exames solicitados, os procedimentos realizados, a prescrição
 * e os pagamentos.
 */
@Entity
@Table(name = "consulta")
@Getter
@Setter
@NoArgsConstructor
public class Consulta extends EntidadeBase {

    private String data;
    private String horario;
    private String queixaPrincipal;
    private String diagnostico;
    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    @JsonIgnoreProperties({"convenios"})
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    @JsonIgnoreProperties({"especialidades"})
    private Medico medico;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prescricao_id")
    private Prescricao prescricao;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "consulta_id")
    private List<Procedimento> procedimentos = new ArrayList<>();

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL)
    private List<Exame> exames = new ArrayList<>();

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL)
    private List<Pagamento> pagamentos = new ArrayList<>();

    public Consulta(String data, String horario, Paciente paciente, Medico medico) {
        this.data = data;
        this.horario = horario;
        this.paciente = paciente;
        this.medico = medico;
        paciente.adicionarConsulta(this);
        medico.adicionarConsulta(this);
    }

    /** Usado pelo endpoint {@code POST /api/consultas/{id}/atendimento}. */
    public void registrarAtendimento(String queixaPrincipal, String diagnostico, String observacoes) {
        this.queixaPrincipal = queixaPrincipal;
        this.diagnostico = diagnostico;
        this.observacoes = observacoes;
    }

    /**
     * Cria um exame solicitado nesta consulta. O exame nasce com a data da
     * consulta e situação SOLICITADO; quem persiste é o {@code ExameService}.
     */
    public Exame solicitarExame(String nome, String tipo) {
        Exame exame = new Exame(nome, tipo, data);
        exame.setConsulta(this);
        exames.add(exame);
        return exame;
    }

    /** Cria a prescrição da consulta (uma por consulta) e liga os dois lados. */
    public Prescricao gerarPrescricao() {
        Prescricao nova = new Prescricao(data);
        nova.setConsulta(this);
        this.prescricao = nova;
        return nova;
    }

    /** Adiciona um procedimento realizado nesta consulta. */
    public void adicionarProcedimento(Procedimento procedimento) {
        procedimentos.add(procedimento);
    }

    /** Adiciona um pagamento referente a esta consulta. */
    public void adicionarPagamento(Pagamento pagamento) {
        pagamento.setConsulta(this);
        pagamentos.add(pagamento);
    }
}
