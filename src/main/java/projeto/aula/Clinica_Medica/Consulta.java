package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consulta")
public class Consulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String data, horario, queixaPrincipal, diagnostico, observacoes;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    @JsonIgnoreProperties({"convenios"})
    public Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    @JsonIgnoreProperties({"especialidades"})
    public Medico medico;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prescricao_id")
    public Prescricao prescricao;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "consulta_id")
    public List<Procedimento> procedimentos = new ArrayList<>();

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL)
    public List<Exame> exames = new ArrayList<>();

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL)
    public List<Pagamento> pagamentos = new ArrayList<>();

    public Consulta() {
    }

    public Consulta(int id, String data, String horario, Paciente paciente, Medico medico) {
        this.id = id; this.data = data; this.horario = horario; this.paciente = paciente; this.medico = medico;
        paciente.consultas.add(this);
        medico.consultas.add(this);
    }

    public void registrarAtend(String queixa, String diagnostico, String observacoes) {
        this.queixaPrincipal = queixa; this.diagnostico = diagnostico; this.observacoes = observacoes;
    }

    public Exame solicitarExa(int id, String nome, String tipo) {
        Exame e = new Exame(id, nome, tipo, data);
        e.consulta = this;
        exames.add(e);
        return e;
    }

    public Prescricao gerarPresc(int id) { prescricao = new Prescricao(id, data); return prescricao; }
}
