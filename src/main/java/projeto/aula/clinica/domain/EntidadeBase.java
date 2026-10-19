package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base de todas as entidades persistidas: define a chave primária e o controle
 * de "registro novo".
 *
 * {@code @MappedSuperclass} faz com que os campos declarados aqui sejam copiados
 * para a tabela de cada entidade filha (esta classe não gera tabela própria),
 * evitando repetir {@code id}/{@code @Id}/{@code @GeneratedValue} em cada classe.
 *
 * Os campos são privados e os acessores são gerados pelo Lombok: o JSON exposto
 * pela API continua sendo o mesmo, pois os nomes das propriedades são idênticos.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class EntidadeBase {

    /** Chave primária gerada pelo banco (PostgreSQL). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /** Indica que o registro ainda não foi gravado (usado para distinguir insert de update). */
    @JsonIgnore
    public boolean isNovo() {
        return id <= 0;
    }
}
