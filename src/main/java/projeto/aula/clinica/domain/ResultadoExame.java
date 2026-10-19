package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado de um exame.
 *
 * A chave estrangeira fica em {@code exame.resultado_id}; por isso aqui o lado
 * é o inverso ({@code mappedBy}).
 */
@Entity
@Table(name = "resultado_exame")
@Getter
@Setter
@NoArgsConstructor
public class ResultadoExame extends EntidadeBase {

    private String data;
    private String descricao;
    private String conclusao;

    @OneToOne(mappedBy = "resultado")
    @JsonIgnoreProperties({"resultado"})
    private Exame exame;

    public ResultadoExame(String data, String descricao, String conclusao) {
        this.data = data;
        this.descricao = descricao;
        this.conclusao = conclusao;
    }
}
