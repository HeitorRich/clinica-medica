/* =========================================================================
   Configuração das telas
   ========================================================================= */

const RESOURCES = {
  pacientes: {
    title: 'Pacientes', singular: 'Paciente', path: '/api/pacientes',
    columns: [
      { path: 'id', label: '#' },
      { path: 'nome', label: 'Nome' },
      { path: 'cpf', label: 'CPF' },
      { path: 'dataNasc', label: 'Nascimento' },
      { path: 'telefone', label: 'Telefone' },
      { path: 'convenios', label: 'Convênios', format: v => (v || []).length ? v.map(c => `${getPath(c, 'convenio.nome') || '?'} (${c.numeroCarteirinha || '—'})`).join(', ') : '—' }
    ],
    fields: [
      { name: 'nome', label: 'Nome completo', required: true, mask: 'letras' },
      { name: 'cpf', label: 'CPF', mask: 'cpf' },
      { name: 'dataNasc', label: 'Data de nascimento', mask: 'data' },
      { name: 'telefone', label: 'Telefone', mask: 'telefone' }
    ],
    actions: [
      { label: 'Histórico', run: p => openHistorico(p) },
      { label: 'Convênios', run: p => openConveniosPaciente(p) }
    ]
  },
  medicos: {
    title: 'Médicos', singular: 'Médico', path: '/api/medicos',
    columns: [
      { path: 'id', label: '#' },
      { path: 'nome', label: 'Nome' },
      { path: 'crm', label: 'CRM' },
      { path: 'especialidades', label: 'Especialidades', format: v => (v || []).map(e => e.nome).join(', ') || '—' }
    ],
    fields: [
      { name: 'nome', label: 'Nome', required: true, mask: 'letras' },
      { name: 'crm', label: 'CRM', mask: 'crm' }
    ],
    actions: [
      { label: 'Agenda', run: m => openAgenda(m) },
      { label: 'Especialidades', run: m => openEspecialidadesMedico(m) }
    ]
  },
  especialidades: {
    title: 'Especialidades', singular: 'Especialidade', path: '/api/especialidades',
    columns: [
      { path: 'id', label: '#' },
      { path: 'nome', label: 'Nome' },
      { path: 'descricao', label: 'Descrição' }
    ],
    fields: [
      { name: 'nome', label: 'Nome', required: true, mask: 'letras' },
      { name: 'descricao', label: 'Descrição', mask: 'texto' }
    ],
    actions: [
      { label: 'Médicos', run: e => openEspecialidadeMedicos(e) }
    ]
  },
  convenios: {
    title: 'Convênios', singular: 'Convênio', path: '/api/convenios',
    columns: [
      { path: 'id', label: '#' },
      { path: 'nome', label: 'Operadora' },
      { path: 'cnpj', label: 'CNPJ' },
      { path: 'codigoAns', label: 'Código ANS' }
    ],
    fields: [
      { name: 'nome', label: 'Nome da operadora', required: true, mask: 'alfanumerico' },
      { name: 'cnpj', label: 'CNPJ da operadora', mask: 'cnpj' },
      { name: 'codigoAns', label: 'Código ANS', mask: '000000' }
    ],
    actions: [
      { label: 'Pacientes', run: c => openConvenioPacientes(c) }
    ]
  },
  medicamentos: {
    title: 'Medicamentos', singular: 'Medicamento', path: '/api/medicamentos',
    columns: [
      { path: 'id', label: '#' },
      { path: 'nome', label: 'Nome' },
      { path: 'dose', label: 'Dose' },
      { path: 'frequencia', label: 'Frequência' },
      { path: 'duracao', label: 'Duração' }
    ],
    fields: [
      { name: 'nome', label: 'Nome', required: true, mask: 'alfanumerico' },
      { name: 'dose', label: 'Dose', mask: 'alfanumerico' },
      { name: 'frequencia', label: 'Frequência', mask: 'alfanumerico' },
      { name: 'duracao', label: 'Duração', mask: 'alfanumerico' }
    ]
  },
  procedimentos: {
    title: 'Procedimentos', singular: 'Procedimento', path: '/api/procedimentos',
    columns: [
      { path: 'id', label: '#' },
      { path: 'nome', label: 'Nome' },
      { path: 'valorPadrao', label: 'Valor padrão', money: true },
      { path: 'valorCobrado', label: 'Valor cobrado', money: true }
    ],
    fields: [
      { name: 'nome', label: 'Nome', required: true, mask: 'alfanumerico' },
      { name: 'valorPadrao', label: 'Valor padrão', type: 'number', mask: 'dinheiro', valueType: 'dinheiro' },
      { name: 'valorCobrado', label: 'Valor cobrado', type: 'number', mask: 'dinheiro', valueType: 'dinheiro' }
    ],
    actions: [
      { label: 'Realizar', run: p => realizarProcedimento(p) }
    ]
  },
  consultas: {
    title: 'Consultas', singular: 'Consulta', path: '/api/consultas',
    columns: [
      { path: 'id', label: '#' },
      { path: 'data', label: 'Data' },
      { path: 'horario', label: 'Hora' },
      { path: 'paciente.nome', label: 'Paciente', format: v => v || '—' },
      { path: 'medico.nome', label: 'Médico', format: v => v || '—' },
      { path: 'diagnostico', label: 'Diagnóstico', format: v => v || '—' }
    ],
    fields: [
      { name: 'paciente.id', label: 'Paciente', type: 'number', required: true, options: () => state.pacientes.map(p => ({ value: p.id, label: `#${p.id} · ${p.nome}` })) },
      { name: 'medico.id', label: 'Médico', type: 'number', required: true, options: () => state.medicos.map(m => ({ value: m.id, label: `#${m.id} · ${m.nome}` })) },
      { name: 'data', label: 'Data (dd/mm/aaaa)', mask: 'data' },
      { name: 'horario', label: 'Horário (hh:mm)', mask: 'hora' }
    ],
    actions: [
      { label: 'Abrir atendimento', run: c => openConsulta(c) }
    ]
  },
  exames: {
    title: 'Exames', singular: 'Exame', path: '/api/exames',
    columns: [
      { path: 'id', label: '#' },
      { path: 'nome', label: 'Exame' },
      { path: 'tipo', label: 'Tipo' },
      { path: 'consulta.id', label: 'Consulta', format: (v, item) => referencia(v, getPath(item, 'consulta.data')) },
      { path: 'consulta.paciente.nome', label: 'Paciente', format: v => v || '—' },
      { path: 'dataSolicitacao', label: 'Solicitado em' },
      { path: 'situacao', label: 'Situação', badge: true }
    ],
    fields: [
      { name: 'nome', label: 'Nome', required: true, mask: 'alfanumerico' },
      { name: 'tipo', label: 'Tipo', mask: 'alfanumerico' },
      {
        name: 'consulta.id', label: 'Consulta de origem', type: 'number',
        options: () => state.consultas.map(c => ({
          value: c.id,
          label: `#${c.id} · ${getPath(c, 'paciente.nome') || 'sem paciente'} · ${c.data || 'sem data'}`
        }))
      },
      { name: 'dataSolicitacao', label: 'Data de solicitação', mask: 'data' }
    ],
    actions: [
      /* "when" esconde a ação quando o exame já tem resultado (situação CONCLUIDO). */
      { label: 'Resultado', when: e => !e.resultado, run: e => registrarResultado(e) }
    ]
  },
  'resultados-exame': {
    title: 'Resultados de Exame', singular: 'Resultado', path: '/api/resultados-exame',
    columns: [
      { path: 'id', label: '#' },
      { path: 'data', label: 'Data' },
      { path: 'exame.id', label: 'Exame', format: (v, item) => referencia(v, getPath(item, 'exame.nome')) },
      { path: 'exame.consulta.paciente.nome', label: 'Paciente', format: v => v || '—' },
      { path: 'descricao', label: 'Descrição' },
      { path: 'conclusao', label: 'Conclusão' }
    ],
    fields: [
      { name: 'data', label: 'Data', mask: 'data' },
      { name: 'descricao', label: 'Descrição', type: 'textarea', full: true, mask: 'texto' },
      { name: 'conclusao', label: 'Conclusão', type: 'textarea', full: true, mask: 'texto' }
    ]
  },
  prescricoes: {
    title: 'Prescrições', singular: 'Prescrição', path: '/api/prescricoes',
    columns: [
      { path: 'id', label: '#' },
      { path: 'data', label: 'Data' },
      { path: 'consulta.id', label: 'Consulta', format: (v, item) => referencia(v, getPath(item, 'consulta.data')) },
      { path: 'consulta.paciente.nome', label: 'Paciente', format: v => v || '—' },
      { path: 'medicamentos', label: 'Medicamentos', format: v => (v || []).map(m => m.nome).join(', ') || '—' }
    ],
    fields: [
      { name: 'data', label: 'Data', mask: 'data' }
    ],
    actions: [
      { label: 'Medicamentos', run: p => openMedicamentosPrescricao(p) }
    ]
  },
  pagamentos: {
    title: 'Pagamentos', singular: 'Pagamento', path: '/api/pagamentos',
    columns: [
      { path: 'id', label: '#' },
      { path: 'consulta.id', label: 'Consulta', format: (v, item) => referencia(v, getPath(item, 'consulta.data')) },
      { path: 'consulta.paciente.nome', label: 'Paciente', format: v => v || '—' },
      { path: 'data', label: 'Data' },
      { path: 'valor', label: 'Valor', money: true },
      { path: 'formaPagamento', label: 'Forma' },
      { path: 'situacao', label: 'Situação', badge: true }
    ],
    fields: [
      {
        name: 'consulta.id', label: 'Consulta', type: 'number',
        options: () => state.consultas.map(c => ({
          value: c.id,
          label: `#${c.id} · ${getPath(c, 'paciente.nome') || 'sem paciente'} · ${c.data || 'sem data'}`
        }))
      },
      { name: 'data', label: 'Data', mask: 'data' },
      { name: 'valor', label: 'Valor', type: 'number', mask: 'dinheiro', valueType: 'dinheiro' },
      { name: 'formaPagamento', label: 'Forma de pagamento', type: 'select', options: () => ['PIX', 'DINHEIRO', 'CARTAO', 'CONVENIO'].map(f => ({ value: f, label: f })) }
    ],
    actions: [
      /* Cada ação só aparece quando realmente muda a situação. */
      { label: 'Pagar', when: p => p.situacao !== 'PAGO', run: p => pagamentoAcao(p, 'pagar') },
      { label: 'Estornar', when: p => p.situacao !== 'ESTORNADO', run: p => pagamentoAcao(p, 'estornar') }
    ]
  }
};

const NAV = [
  { group: 'Visão geral', items: [{ key: 'dashboard', title: 'Dashboard', icon: '📊' }] },
  {
    group: 'Cadastros', items: [
      { key: 'pacientes', title: 'Pacientes', icon: '👤' },
      { key: 'medicos', title: 'Médicos', icon: '🩺' },
      { key: 'especialidades', title: 'Especialidades', icon: '🏷️' },
      { key: 'convenios', title: 'Convênios', icon: '📋' },
      { key: 'medicamentos', title: 'Medicamentos', icon: '💊' },
      { key: 'procedimentos', title: 'Procedimentos', icon: '🧾' }
    ]
  },
  {
    group: 'Atendimento', items: [
      { key: 'consultas', title: 'Consultas', icon: '📅' },
      { key: 'exames', title: 'Exames', icon: '🔬' },
      { key: 'resultados-exame', title: 'Resultados', icon: '📄' },
      { key: 'prescricoes', title: 'Prescrições', icon: '🖊️' },
      { key: 'pagamentos', title: 'Pagamentos', icon: '💳' }
    ]
  }
];
