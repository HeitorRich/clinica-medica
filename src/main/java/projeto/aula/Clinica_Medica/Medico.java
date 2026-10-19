package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medico")
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String nome, crm;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "medico_especialidade",
            joinColumns = @JoinColumn(name = "medico_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidade_id"))
    public List<Especialidade> especialidades = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "medico")
    public List<Consulta> consultas = new ArrayList<>();

    public Medico() {
    }

    public Medico(int id, String nome, String crm) { this.id = id; this.nome = nome; this.crm = crm; }

    public void adicionarEspecialidade(Especialidade e) { especialidades.add(e); e.medicos.add(this); }

    public List<Consulta> consultarAgenda() { return consultas; }
}
