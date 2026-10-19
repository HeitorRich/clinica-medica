package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Convenio;
import projeto.aula.clinica.domain.Paciente;
import projeto.aula.clinica.domain.PacienteConvenio;
import projeto.aula.clinica.repository.ConvenioRepository;

import java.util.List;

/**
 * Catálogo de operadoras (convênios).
 */
@Service
public class ConvenioService extends CrudService<Convenio> {

    public ConvenioService(ConvenioRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Convenio origem, Convenio destino) {
        destino.setCnpj(origem.getCnpj());
        destino.setNome(origem.getNome());
        destino.setCodigoAns(origem.getCodigoAns());
    }

    /** Pacientes que utilizam esta operadora. */
    public List<Paciente> listarPacientes(int convenioId) {
        return buscarObrigatorio(convenioId).consultarPacientes();
    }

    /** Vínculos (paciente + número de carteirinha) que usam esta operadora. */
    public List<PacienteConvenio> listarVinculos(int convenioId) {
        return buscarObrigatorio(convenioId).getVinculos();
    }
}
