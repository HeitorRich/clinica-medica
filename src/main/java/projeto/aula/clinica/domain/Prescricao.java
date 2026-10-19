package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Prescrição gerada em uma consulta: a data e a lista de medicamentos.
 */
@Entity
@Table(name = "prescricao")
@Getter
@Setter
@NoArgsConstructor
public class Prescricao extends EntidadeBase {

    private String data;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "prescricao_id")
    private List<Medicamento> medicamentos = new ArrayList<>();

    /**
     * Consulta que gerou esta prescrição. A chave estrangeira está em
     * {@code consulta.prescricao_id}, por isso aqui este é o lado inverso.
     */
    @OneToOne(mappedBy = "prescricao")
    @JsonIgnoreProperties({"exames", "procedimentos", "pagamentos", "prescricao"})
    private Consulta consulta;

    public Prescricao(String data) {
        this.data = data;
    }

    /** Usado pelo endpoint {@code POST /api/prescricoes/{id}/medicamentos}. */
    public void adicionarMedicamento(Medicamento medicamento) {
        medicamentos.add(medicamento);
    }
}
