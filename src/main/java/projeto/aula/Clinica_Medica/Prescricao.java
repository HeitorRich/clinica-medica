package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescricao")
public class Prescricao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String data;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "prescricao_id")
    public List<Medicamento> medicamentos = new ArrayList<>();

    /**
     * Consulta que gerou esta prescrição (a FK fica em consulta.prescricao_id,
     * por isso esta é a ponta inversa). As coleções da consulta são omitidas na
     * serialização para não repetir dados nem gerar referência circular.
     */
    @OneToOne(mappedBy = "prescricao")
    @JsonIgnoreProperties({"exames", "procedimentos", "pagamentos", "prescricao"})
    public Consulta consulta;

    public Prescricao() {
    }

    public Prescricao(int id, String data) { this.id = id; this.data = data; }

    public void adicionarMedicamento(Medicamento m) { medicamentos.add(m); }
}
