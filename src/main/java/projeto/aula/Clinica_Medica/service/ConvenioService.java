package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Convenio;
import projeto.aula.Clinica_Medica.repository.ConvenioRepository;

@Service
public class ConvenioService extends AbstractCrudService<Convenio> {

    public ConvenioService(ConvenioRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Convenio origem, Convenio destino) {
        destino.cnpj = origem.cnpj;
        destino.nome = origem.nome;
        destino.codigoAns = origem.codigoAns;
    }
}
