package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Medico;
import projeto.aula.Clinica_Medica.repository.MedicoRepository;

@Service
public class MedicoService extends AbstractCrudService<Medico> {

    public MedicoService(MedicoRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Medico origem, Medico destino) {
        destino.nome = origem.nome;
        destino.crm = origem.crm;
    }
}
