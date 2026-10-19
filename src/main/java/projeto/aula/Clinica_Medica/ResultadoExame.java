package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "resultado_exame")
public class ResultadoExame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String data, descricao, conclusao;

    /**
     * Exame a que este resultado pertence (a FK fica em exame.resultado_id, por
     * isso esta é a ponta inversa). É por aqui que se sabe de que exame/paciente
     * é o resultado.
     */
    @OneToOne(mappedBy = "resultado")
    @JsonIgnoreProperties({"resultado"})
    public Exame exame;

    public ResultadoExame() {
    }

    public ResultadoExame(int id, String data, String descricao, String conclusao) {
        this.id = id; this.data = data; this.descricao = descricao; this.conclusao = conclusao;
    }
}
