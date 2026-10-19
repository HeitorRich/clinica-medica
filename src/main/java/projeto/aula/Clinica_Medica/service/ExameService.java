package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Exame;
import projeto.aula.Clinica_Medica.repository.ExameRepository;

@Service
public class ExameService extends AbstractCrudService<Exame> {

    public ExameService(ExameRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Exame origem, Exame destino) {
        destino.nome = origem.nome;
        destino.tipo = origem.tipo;
        destino.dataSolicitacao = origem.dataSolicitacao;
        destino.consulta = origem.consulta;
    }
}
