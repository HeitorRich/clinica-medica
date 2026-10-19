/* =========================================================================
   Tela principal (index.html): motor generico de navegacao, tabelas e
   formularios. A descricao das entidades fica em js/recursos.js; os
   utilitarios em js/comum.js; o acesso HTTP em js/api.js.
   ========================================================================= */

/* ---------- Estado da tela ---------- */

const state = Object.fromEntries(Object.keys(RESOURCES).map(k => [k, []]));
let currentView = 'dashboard';

/* ---------- Formatacao de valores ---------- */

function fmtMoney(v) {
  if (v == null || v === '') return '—';
  return 'R$ ' + Number(v).toFixed(2).replace('.', ',');
}
function badge(value) {
  const val = value == null ? '—' : String(value);
  const cls = 'badge-' + val.toLowerCase().replace(/[^a-z]/g, '');
  return `<span class="badge ${cls}">${escapeHtml(val)}</span>`;
}

/* ---------- Comunicacao com a API ---------- */

function toast(message, type = 'info') {
  const root = document.getElementById('toast-root');
  const el = document.createElement('div');
  el.className = 'toast ' + type;
  el.textContent = message;
  root.appendChild(el);
  setTimeout(() => el.remove(), 3200);
}

async function api(method, url, body) {
  const { response, data } = await requisicao(method, url, body);
  if (!response.ok) {
    // ProblemDetail do Spring usa "title"/"detail"; mantemos "message" por compatibilidade
    const msg = mensagemDeErro(data, response);
    toast(msg, 'error');
    throw new Error(msg);
  }
  return data;
}

/* ---------- Carga dos dados ---------- */

async function loadState() {
  const keys = Object.keys(RESOURCES);
  const results = await Promise.all(keys.map(k => api('GET', RESOURCES[k].path).catch(() => [])));
  keys.forEach((k, i) => { state[k] = results[i] || []; });
}
async function refresh() {
  await loadState();
  navigate(currentView);
}

/* =========================================================================
   Modal genérico
   ========================================================================= */

function openModal(title, bodyHtml, footHtml, wide) {
  const root = document.getElementById('modal-root');
  root.innerHTML = `
    <div class="modal-overlay" id="overlay">
      <div class="modal ${wide ? 'wide' : ''}">
        <div class="modal-head">
          <h3>${escapeHtml(title)}</h3>
          <button class="close" type="button" data-close>&times;</button>
        </div>
        <div class="modal-body">${bodyHtml}</div>
        ${footHtml ? `<div class="modal-foot">${footHtml}</div>` : ''}
      </div>
    </div>`;
  root.querySelectorAll('[data-close]').forEach(b => b.onclick = closeModal);
  document.getElementById('overlay').onclick = e => { if (e.target.id === 'overlay') closeModal(); };
}
function closeModal() { document.getElementById('modal-root').innerHTML = ''; }

/* =========================================================================
   Formulários
   ========================================================================= */

function normalizeOptions(options) {
  return (options || []).map(o => (typeof o === 'object' ? o : { value: o, label: o }));
}
function fieldId(name) { return 'f-' + name.replace(/[^a-zA-Z0-9_-]/g, '-'); }

function fieldHtml(field, value) {
  const id = fieldId(field.name);
  const req = field.required ? ' *' : '';
  const label = `<label for="${id}">${escapeHtml(field.label)}${req}</label>`;
  const wrap = field.full ? 'field full' : 'field';
  if (field.options) {
    const opts = normalizeOptions(field.options());
    const optsHtml = ['<option value="">— selecione —</option>']
      .concat(opts.map(o => `<option value="${escapeHtml(o.value)}" ${String(o.value) === String(value ?? '') ? 'selected' : ''}>${escapeHtml(o.label)}</option>`))
      .join('');
    return `<div class="${wrap}">${label}<select id="${id}" name="${field.name}" ${field.required ? 'required' : ''}>${optsHtml}</select></div>`;
  }
  if (field.type === 'textarea') {
    return `<div class="${wrap}">${label}<textarea id="${id}" name="${field.name}" rows="2" ${Mask.atributos(field)}>${escapeHtml(value ?? '')}</textarea></div>`;
  }
  const money = field.valueType === 'dinheiro';
  const type = money ? 'text' : (field.type || 'text');
  const inputValue = money ? Mask.formatarMoeda(value) : (value ?? '');
  return `<div class="${wrap}">${label}<input id="${id}" name="${field.name}" type="${type}" value="${escapeHtml(inputValue)}" ${Mask.atributos(field)} ${field.required ? 'required' : ''}></div>`;
}

/** Converte o texto do campo para o tipo esperado pela API. */
function valorDoCampo(field, raw) {
  if (field.valueType === 'dinheiro') {
    const numero = Mask.valorNumerico(raw);
    return isNaN(numero) ? raw : numero;
  }
  return field.type === 'number' ? Number(raw) : raw;
}

function collect(fields, form) {
  const body = {};
  for (const f of fields) {
    const el = form.querySelector('#' + CSS.escape(fieldId(f.name)));
    if (!el) continue;
    const raw = String(el.value).trim();
    if (raw === '') continue;
    setPath(body, f.name, valorDoCampo(f, raw));
  }
  return body;
}

/** Modal com um formulário simples que envia para onSubmit(data). */
function simpleForm(title, fields, onSubmit) {
  const body = `<form id="modal-form" class="form-grid">${fields.map(f => fieldHtml(f, f.value ?? '')).join('')}</form>`;
  const foot = `<button class="btn ghost" type="button" data-close>Cancelar</button><button class="btn" type="button" id="modal-save">Salvar</button>`;
  openModal(title, body, foot);
  document.getElementById('modal-save').onclick = async () => {
    const form = document.getElementById('modal-form');
    if (!form.reportValidity()) return;
    await onSubmit(collect(fields, form));
  };
}

function openForm(key, item) {
  const res = RESOURCES[key];
  const isEdit = !!item;
  const fieldsHtml = res.fields.map(f => fieldHtml(f, isEdit ? getPath(item, f.name) : '')).join('');
  openModal(`${isEdit ? 'Editar' : 'Novo(a)'} ${res.singular}`,
    `<form id="modal-form" class="form-grid">${fieldsHtml}</form>`,
    `<button class="btn ghost" type="button" data-close>Cancelar</button><button class="btn" type="button" id="modal-save">${isEdit ? 'Salvar' : 'Cadastrar'}</button>`);
  document.getElementById('modal-save').onclick = async () => {
    const form = document.getElementById('modal-form');
    if (!form.reportValidity()) return;
    const data = collect(res.fields, form);
    await api(isEdit ? 'PUT' : 'POST', isEdit ? `${res.path}/${item.id}` : res.path, data);
    toast(isEdit ? 'Registro atualizado' : 'Registro cadastrado', 'success');
    closeModal();
    await refresh();
  };
}

/* =========================================================================
   Tabela genérica
   ========================================================================= */

function cell(col, item) {
  const raw = getPath(item, col.path);
  if (col.badge) return badge(raw);
  if (col.money) return fmtMoney(raw);
  if (col.format) return escapeHtml(col.format(raw, item));
  if (raw == null || raw === '') return '<span class="muted">—</span>';
  return escapeHtml(raw);
}

function renderResource(key, filter = '') {
  const res = RESOURCES[key];
  const term = filter.trim().toLowerCase();
  let items = state[key] || [];
  if (term) items = items.filter(i => JSON.stringify(i).toLowerCase().includes(term));

  const rows = items.length
    ? items.map(item => `
        <tr>
          ${res.columns.map(c => `<td>${cell(c, item)}</td>`).join('')}
          <td><div class="row-actions">
            ${(res.actions || [])
              .map((a, i) => ({ a, i }))
              .filter(({ a }) => !a.when || a.when(item))
              .map(({ a, i }) => `<button class="mini" data-act="${i}" data-id="${item.id}">${escapeHtml(a.label)}</button>`)
              .join('')}
            <button class="mini" data-act="edit" data-id="${item.id}">Editar</button>
            <button class="mini danger" data-act="del" data-id="${item.id}">Excluir</button>
          </div></td>
        </tr>`).join('')
    : `<tr><td colspan="${res.columns.length + 1}" class="empty">Nenhum registro encontrado.</td></tr>`;

  document.getElementById('main').innerHTML = `
    <div class="view-head">
      <h2>${escapeHtml(res.title)} <span class="muted">(${(state[key] || []).length})</span></h2>
      <div class="tools">
        <input class="search" id="search" placeholder="Buscar..." value="${escapeHtml(filter)}">
        <button class="btn" id="new-btn">+ Novo</button>
      </div>
    </div>
    <div class="card" style="padding:0; overflow:hidden;">
      <table>
        <thead><tr>${res.columns.map(c => `<th>${escapeHtml(c.label)}</th>`).join('')}<th>Ações</th></tr></thead>
        <tbody id="tbody">${rows}</tbody>
      </table>
    </div>`;

  document.getElementById('new-btn').onclick = () => openForm(key);
  const search = document.getElementById('search');
  search.oninput = () => { const pos = search.selectionStart; renderResource(key, search.value); const s = document.getElementById('search'); s.focus(); s.setSelectionRange(pos, pos); };

  document.getElementById('tbody').onclick = async e => {
    const btn = e.target.closest('button[data-act]');
    if (!btn) return;
    const id = Number(btn.dataset.id);
    const item = items.find(x => x.id === id);
    const act = btn.dataset.act;
    try {
      if (act === 'edit') return openForm(key, item);
      if (act === 'del') {
        if (!confirm(`Excluir o registro #${id}?`)) return;
        await api('DELETE', `${res.path}/${id}`);
        toast('Registro excluído', 'success');
        return await refresh();
      }
      await res.actions[Number(act)].run(item);
    } catch (err) { /* erro já exibido em toast */ }
  };
}

/* =========================================================================
   Dashboard
   ========================================================================= */

function renderDashboard() {
  const pacientes = state.pacientes, medicos = state.medicos, consultas = state.consultas;
  const exames = state.exames, pagamentos = state.pagamentos;
  const examesPendentes = exames.filter(e => e.situacao !== 'CONCLUIDO');
  const pagPendentes = pagamentos.filter(p => p.situacao === 'PENDENTE');
  const faturado = pagamentos.filter(p => p.situacao === 'PAGO').reduce((s, p) => s + (Number(p.valor) || 0), 0);

  const ultimas = [...consultas].reverse().slice(0, 6);
  const ultimasRows = ultimas.length
    ? ultimas.map(c => `<tr>
        <td>#${c.id}</td>
        <td>${escapeHtml(getPath(c, 'paciente.nome') || '—')}</td>
        <td>${escapeHtml(getPath(c, 'medico.nome') || '—')}</td>
        <td>${escapeHtml(c.data || '')} ${escapeHtml(c.horario || '')}</td>
        <td><button class="mini" data-open="${c.id}">Abrir</button></td>
      </tr>`).join('')
    : `<tr><td colspan="5" class="empty">Sem consultas.</td></tr>`;

  const pendRows = examesPendentes.length
    ? examesPendentes.slice(0, 6).map(e => `<tr>
        <td>#${e.id} ${escapeHtml(e.nome || '')}</td>
        <td>${escapeHtml(getPath(e, 'consulta.paciente.nome') || '—')}</td>
        <td>${badge(e.situacao)}</td>
        <td><button class="mini" data-resultado="${e.id}">Resultado</button></td>
      </tr>`).join('')
    : `<tr><td colspan="4" class="empty">Nenhum exame pendente.</td></tr>`;

  document.getElementById('main').innerHTML = `
    <div class="metrics">
      <div class="metric"><div class="label">Pacientes</div><div class="value">${pacientes.length}</div><div class="hint">cadastrados</div></div>
      <div class="metric"><div class="label">Médicos</div><div class="value">${medicos.length}</div><div class="hint">ativos</div></div>
      <div class="metric"><div class="label">Consultas</div><div class="value">${consultas.length}</div><div class="hint">agendadas</div></div>
      <div class="metric"><div class="label">Exames pendentes</div><div class="value">${examesPendentes.length}</div><div class="hint">aguardando resultado</div></div>
      <div class="metric"><div class="label">Pagamentos pendentes</div><div class="value">${pagPendentes.length}</div><div class="hint">em aberto</div></div>
      <div class="metric"><div class="label">Faturamento pago</div><div class="value" style="font-size:20px">${fmtMoney(faturado)}</div><div class="hint">${pagamentos.filter(p => p.situacao === 'PAGO').length} pagamento(s)</div></div>
    </div>
    <div class="two-col">
      <div class="card">
        <h3>Consultas recentes</h3>
        <table><thead><tr><th>#</th><th>Paciente</th><th>Médico</th><th>Data</th><th></th></tr></thead>
        <tbody id="dash-consultas">${ultimasRows}</tbody></table>
      </div>
      <div class="card">
        <h3>Exames a concluir</h3>
        <table><thead><tr><th>Exame</th><th>Paciente</th><th>Status</th><th></th></tr></thead>
        <tbody id="dash-exames">${pendRows}</tbody></table>
      </div>
    </div>`;

  document.getElementById('dash-consultas').onclick = e => {
    const b = e.target.closest('button[data-open]'); if (b) openConsulta(consultas.find(c => c.id === Number(b.dataset.open)));
  };
  document.getElementById('dash-exames').onclick = e => {
    const b = e.target.closest('button[data-resultado]'); if (b) registrarResultado(exames.find(x => x.id === Number(b.dataset.resultado)));
  };
}

/* =========================================================================
   Navegação
   ========================================================================= */

function navigate(key) {
  currentView = key;
  document.querySelectorAll('.nav-item').forEach(b => b.classList.toggle('active', b.dataset.key === key));
  if (key === 'dashboard') {
    document.getElementById('page-title').textContent = 'Dashboard';
    document.getElementById('page-sub').textContent = 'Visão geral da clínica';
    renderDashboard();
  } else {
    const res = RESOURCES[key];
    document.getElementById('page-title').textContent = res.title;
    document.getElementById('page-sub').textContent = 'Cadastro e gestão de ' + res.title.toLowerCase();
    renderResource(key);
  }
}

function buildNav() {
  const nav = document.getElementById('nav');
  nav.innerHTML = NAV.map(group => `
    <div class="nav-group">
      <div class="group-title">${escapeHtml(group.group)}</div>
      ${group.items.map(it => `<button class="nav-item" data-key="${it.key}"><span class="icon">${it.icon}</span>${escapeHtml(it.title)}</button>`).join('')}
    </div>`).join('');
  nav.querySelectorAll('.nav-item').forEach(b => b.onclick = () => navigate(b.dataset.key));
}

/* =========================================================================
   Ações específicas
   ========================================================================= */

async function openHistorico(p) {
  const consultas = await api('GET', `/api/pacientes/${p.id}/historico`);
  const rows = consultas.length
    ? consultas.map(c => `<tr>
        <td>#${c.id}</td>
        <td>${escapeHtml(c.data || '')} ${escapeHtml(c.horario || '')}</td>
        <td>${escapeHtml(getPath(c, 'medico.nome') || '—')}</td>
        <td>${escapeHtml(c.diagnostico || '—')}</td>
        <td>${(c.exames || []).length} exame(s)</td>
      </tr>`).join('')
    : `<tr><td colspan="5" class="empty">Sem consultas registradas.</td></tr>`;
  openModal(`Histórico · ${p.nome}`,
    `<div class="table-scroll">
       <table><thead><tr><th>#</th><th>Data</th><th>Médico</th><th>Diagnóstico</th><th>Exames</th></tr></thead><tbody>${rows}</tbody></table>
     </div>`,
    `<button class="btn ghost" type="button" data-close>Fechar</button>`, true);
}

function openConveniosPaciente(p) {
  const vinculados = p.convenios || [];
  const rows = vinculados.length
    ? vinculados.map(c => `<tr>
        <td>${escapeHtml(getPath(c, 'convenio.nome') || '—')}</td>
        <td class="nowrap">${escapeHtml(getPath(c, 'convenio.cnpj') || '—')}</td>
        <td class="nowrap">${escapeHtml(getPath(c, 'convenio.codigoAns') || '—')}</td>
        <td class="nowrap">${escapeHtml(c.numeroCarteirinha || '—')}</td>
        <td>${badge(c.situacao)}</td>
        <td class="nowrap">
          <button class="mini" data-cob="${getPath(c, 'convenio.id')}">Cobertura</button>
          <button class="mini danger" data-remove="${getPath(c, 'convenio.id')}">Desvincular</button>
        </td>
      </tr>`).join('')
    : `<tr><td colspan="6" class="empty">Nenhum convênio vinculado.</td></tr>`;
  const disponiveis = state.convenios.filter(c => !vinculados.some(v => getPath(v, 'convenio.id') === c.id));
  const options = ['<option value="">— selecione —</option>']
    .concat(disponiveis.map(c => `<option value="${c.id}">${escapeHtml(c.nome || '')}${c.codigoAns ? ' · ANS ' + escapeHtml(c.codigoAns) : ''}</option>`)).join('');

  openModal(`Convênios · ${p.nome}`,
    `<div class="table-scroll">
       <table><thead><tr><th>Operadora</th><th>CNPJ</th><th>ANS</th><th>Carteirinha</th><th>Situação</th><th></th></tr></thead><tbody>${rows}</tbody></table>
     </div>
     <div class="inline-form">
       <div class="field"><label>Operadora</label><select id="conv-select">${options}</select></div>
       <div class="field"><label>Nº da carteirinha do paciente</label><input id="conv-carteirinha" data-mask="carteirinha" placeholder="CARD-0001"></div>
       <button class="btn small" type="button" id="conv-add">Vincular</button>
     </div>`,
    `<button class="btn ghost" type="button" data-close>Fechar</button>`, true);

  document.getElementById('conv-add').onclick = async () => {
    const id = document.getElementById('conv-select').value;
    const carteirinha = document.getElementById('conv-carteirinha').value.trim();
    if (!id) return toast('Selecione a operadora', 'error');
    if (!carteirinha) return toast('Informe o número da carteirinha', 'error');
    await api('POST', `/api/pacientes/${p.id}/convenios/${id}?numeroCarteirinha=${encodeURIComponent(carteirinha)}`);
    toast('Convênio vinculado', 'success');
    await loadState();
    openConveniosPaciente(state.pacientes.find(x => x.id === p.id));
  };
  document.querySelectorAll('[data-cob]').forEach(b => b.onclick = () => validarCobertura(p.id, b.dataset.cob));
  document.querySelectorAll('[data-remove]').forEach(b => b.onclick = async () => {
    await api('DELETE', `/api/pacientes/${p.id}/convenios/${b.dataset.remove}`);
    toast('Convênio desvinculado', 'success');
    await loadState();
    openConveniosPaciente(state.pacientes.find(x => x.id === p.id));
  });
}

async function openConvenioPacientes(c) {
  const vinculos = await api('GET', `/api/convenios/${c.id}/vinculos`);
  const rows = vinculos.length
    ? vinculos.map(v => `<tr>
        <td>#${getPath(v, 'paciente.id')}</td>
        <td>${escapeHtml(getPath(v, 'paciente.nome') || '—')}</td>
        <td class="nowrap">${escapeHtml(getPath(v, 'paciente.cpf') || '—')}</td>
        <td class="nowrap">${escapeHtml(v.numeroCarteirinha || '—')}</td>
        <td>${badge(v.situacao)}</td>
      </tr>`).join('')
    : `<tr><td colspan="5" class="empty">Nenhum paciente utiliza este convênio.</td></tr>`;
  openModal(`Pacientes · ${c.nome}`,
    `<div class="table-scroll">
       <table><thead><tr><th>#</th><th>Paciente</th><th>CPF</th><th>Carteirinha</th><th>Situação</th></tr></thead><tbody>${rows}</tbody></table>
     </div>`,
    `<button class="btn ghost" type="button" data-close>Fechar</button>`, true);
}

async function openAgenda(m) {
  const consultas = await api('GET', `/api/medicos/${m.id}/agenda`);
  const rows = consultas.length
    ? consultas.map(c => `<tr>
        <td>#${c.id}</td>
        <td>${escapeHtml(c.data || '')} ${escapeHtml(c.horario || '')}</td>
        <td>${escapeHtml(getPath(c, 'paciente.nome') || '—')}</td>
        <td>${escapeHtml(c.diagnostico || '—')}</td>
      </tr>`).join('')
    : `<tr><td colspan="4" class="empty">Agenda vazia.</td></tr>`;
  openModal(`Agenda · ${m.nome}`,
    `<table><thead><tr><th>#</th><th>Data</th><th>Paciente</th><th>Diagnóstico</th></tr></thead><tbody>${rows}</tbody></table>`,
    `<button class="btn ghost" type="button" data-close>Fechar</button>`);
}

function openEspecialidadesMedico(m) {
  const atuais = m.especialidades || [];
  const rows = atuais.length
    ? atuais.map(e => `<tr><td>#${e.id}</td><td>${escapeHtml(e.nome)}</td>
        <td><button class="mini danger" data-remove="${e.id}">Remover</button></td></tr>`).join('')
    : `<tr><td colspan="3" class="empty">Nenhuma especialidade.</td></tr>`;
  const disponiveis = state.especialidades.filter(e => !atuais.some(a => a.id === e.id));
  const options = ['<option value="">— selecione —</option>']
    .concat(disponiveis.map(e => `<option value="${e.id}">${escapeHtml(e.nome)}</option>`)).join('');

  openModal(`Especialidades · ${m.nome}`,
    `<table><thead><tr><th>#</th><th>Nome</th><th></th></tr></thead><tbody>${rows}</tbody></table>
     <div class="inline-form">
       <div class="field"><label>Adicionar especialidade</label><select id="esp-select">${options}</select></div>
       <button class="btn small" type="button" id="esp-add">Adicionar</button>
     </div>`,
    `<button class="btn ghost" type="button" data-close>Fechar</button>`);

  document.getElementById('esp-add').onclick = async () => {
    const id = document.getElementById('esp-select').value;
    if (!id) return;
    await api('POST', `/api/medicos/${m.id}/especialidades/${id}`);
    toast('Especialidade adicionada', 'success');
    await loadState();
    openEspecialidadesMedico(state.medicos.find(x => x.id === m.id));
  };
  document.querySelectorAll('[data-remove]').forEach(b => b.onclick = async () => {
    await api('DELETE', `/api/medicos/${m.id}/especialidades/${b.dataset.remove}`);
    toast('Especialidade removida', 'success');
    await loadState();
    openEspecialidadesMedico(state.medicos.find(x => x.id === m.id));
  });
}

async function openEspecialidadeMedicos(e) {
  const medicos = await api('GET', `/api/especialidades/${e.id}/medicos`);
  const rows = medicos.length
    ? medicos.map(m => `<tr><td>#${m.id}</td><td>${escapeHtml(m.nome)}</td><td>${escapeHtml(m.crm || '—')}</td></tr>`).join('')
    : `<tr><td colspan="3" class="empty">Nenhum médico vinculado.</td></tr>`;
  openModal(`Médicos · ${e.nome}`,
    `<table><thead><tr><th>#</th><th>Nome</th><th>CRM</th></tr></thead><tbody>${rows}</tbody></table>`,
    `<button class="btn ghost" type="button" data-close>Fechar</button>`);
}

async function validarCobertura(pacienteId, convenioId) {
  const ativa = await api('GET', `/api/pacientes/${pacienteId}/convenios/${convenioId}/cobertura`);
  toast(ativa ? 'Cobertura ATIVA para este vínculo' : 'Vínculo sem cobertura (inativo)', ativa ? 'success' : 'error');
}

async function realizarProcedimento(p) {
  await api('POST', `/api/procedimentos/${p.id}/realizar`);
  toast(`Procedimento "${p.nome}" realizado`, 'success');
}

async function pagamentoAcao(p, acao) {
  await api('POST', `/api/pagamentos/${p.id}/${acao}`);
  toast(acao === 'pagar' ? 'Pagamento confirmado' : 'Pagamento estornado', 'success');
  await refresh();
}

function registrarResultado(exame) {
  simpleForm(`Resultado · ${exame.nome}`, [
    { name: 'data', label: 'Data do resultado', value: new Date().toLocaleDateString('pt-BR'), mask: 'data' },
    { name: 'descricao', label: 'Descrição', type: 'textarea', full: true, mask: 'texto' },
    { name: 'conclusao', label: 'Conclusão', type: 'textarea', full: true, mask: 'texto' }
  ], async data => {
    await api('POST', `/api/exames/${exame.id}/resultado`, data);
    toast('Resultado registrado', 'success');
    closeModal();
    await refresh();
  });
}

function openMedicamentosPrescricao(p) {
  const meds = p.medicamentos || [];
  const rows = meds.length
    ? meds.map(m => `<tr><td>#${m.id}</td><td>${escapeHtml(m.nome)}</td><td>${escapeHtml(m.dose || '—')}</td><td>${escapeHtml(m.frequencia || '—')}</td><td>${escapeHtml(m.duracao || '—')}</td></tr>`).join('')
    : `<tr><td colspan="5" class="empty">Nenhum medicamento.</td></tr>`;

  openModal(`Prescrição #${p.id}`,
    `<div class="table-scroll">
       <table><thead><tr><th>#</th><th>Medicamento</th><th>Dose</th><th>Frequência</th><th>Duração</th></tr></thead><tbody>${rows}</tbody></table>
     </div>
     <div class="inline-form">
       <div class="field"><label>Medicamento</label><input id="med-nome" data-mask="alfanumerico"></div>
       <div class="field narrow"><label>Dose</label><input id="med-dose" data-mask="alfanumerico"></div>
       <div class="field narrow"><label>Frequência</label><input id="med-freq" data-mask="alfanumerico"></div>
       <div class="field narrow"><label>Duração</label><input id="med-dur" data-mask="alfanumerico"></div>
       <button class="btn small" type="button" id="med-add">Adicionar</button>
     </div>`,
    `<button class="btn ghost" type="button" data-close>Fechar</button>`, true);

  document.getElementById('med-add').onclick = async () => {
    const nome = document.getElementById('med-nome').value.trim();
    if (!nome) return;
    await api('POST', `/api/prescricoes/${p.id}/medicamentos`, {
      nome,
      dose: document.getElementById('med-dose').value,
      frequencia: document.getElementById('med-freq').value,
      duracao: document.getElementById('med-dur').value
    });
    toast('Medicamento adicionado', 'success');
    await loadState();
    openMedicamentosPrescricao(state.prescricoes.find(x => x.id === p.id));
  };
}

/* =========================================================================
   Atendimento da consulta (modal completo)
   ========================================================================= */

async function openConsulta(ref) {
  const c = await api('GET', `/api/consultas/${ref.id}`);

  const examesRows = (c.exames || []).length
    ? c.exames.map(e => `<tr>
        <td>#${e.id}</td><td>${escapeHtml(e.nome || '')}</td><td>${escapeHtml(e.tipo || '—')}</td>
        <td>${badge(e.situacao)}</td>
        <td>${e.resultado ? escapeHtml(e.resultado.conclusao || e.resultado.descricao || '—') : '<span class="muted">—</span>'}</td>
      </tr>`).join('')
    : `<tr><td colspan="5" class="empty">Nenhum exame solicitado.</td></tr>`;

  const procRows = (c.procedimentos || []).length
    ? c.procedimentos.map(p => `<tr><td>#${p.id}</td><td>${escapeHtml(p.nome || '')}</td><td>${fmtMoney(p.valorCobrado)}</td></tr>`).join('')
    : `<tr><td colspan="3" class="empty">Nenhum procedimento.</td></tr>`;

  const pagRows = (c.pagamentos || []).length
    ? c.pagamentos.map(p => `<tr>
        <td>#${p.id}</td><td>${escapeHtml(p.data || '')}</td><td>${fmtMoney(p.valor)}</td>
        <td>${escapeHtml(p.formaPagamento || '—')}</td><td>${badge(p.situacao)}</td>
        <td>
          ${p.situacao !== 'PAGO' ? `<button class="mini" data-pag="${p.id}" data-acao="pagar">Pagar</button>` : ''}
          ${p.situacao !== 'ESTORNADO' ? `<button class="mini danger" data-pag="${p.id}" data-acao="estornar">Estornar</button>` : ''}
        </td>
      </tr>`).join('')
    : `<tr><td colspan="6" class="empty">Nenhum pagamento.</td></tr>`;

  const prescricaoHtml = c.prescricao
    ? `<div class="section-head"><h4>Prescrição #${c.prescricao.id}</h4></div>
       <table><thead><tr><th>#</th><th>Medicamento</th><th>Dose</th><th>Frequência</th><th>Duração</th></tr></thead>
         <tbody>${(c.prescricao.medicamentos || []).length
            ? c.prescricao.medicamentos.map(m => `<tr><td>#${m.id}</td><td>${escapeHtml(m.nome)}</td><td>${escapeHtml(m.dose || '—')}</td><td>${escapeHtml(m.frequencia || '—')}</td><td>${escapeHtml(m.duracao || '—')}</td></tr>`).join('')
            : `<tr><td colspan="5" class="empty">Nenhum medicamento.</td></tr>`}</tbody></table>
       <div class="inline-form">
         <div class="field"><label>Medicamento</label><input id="c-med-nome" data-mask="alfanumerico"></div>
         <div class="field narrow"><label>Dose</label><input id="c-med-dose" data-mask="alfanumerico"></div>
         <div class="field narrow"><label>Frequência</label><input id="c-med-freq" data-mask="alfanumerico"></div>
         <div class="field narrow"><label>Duração</label><input id="c-med-dur" data-mask="alfanumerico"></div>
         <button class="btn small" type="button" id="c-med-add">Adicionar</button>
       </div>`
    : `<div class="section-head"><h4>Prescrição</h4><button class="btn small" type="button" id="c-gerar-presc">Gerar prescrição</button></div>
       <p class="muted" style="font-size:13px;margin:0">Nenhuma prescrição gerada para esta consulta.</p>`;

  const body = `
    <div class="detail-head">
      <div class="d"><b>Paciente</b>${escapeHtml(getPath(c, 'paciente.nome') || '—')}</div>
      <div class="d"><b>Médico</b>${escapeHtml(getPath(c, 'medico.nome') || '—')}</div>
      <div class="d"><b>Data/Hora</b>${escapeHtml(c.data || '')} ${escapeHtml(c.horario || '')}</div>
    </div>

    <div class="section">
      <h4>Atendimento</h4>
      <div class="form-grid">
        <div class="field full"><label>Queixa principal</label><input id="c-queixa" data-mask="texto" value="${escapeHtml(c.queixaPrincipal || '')}"></div>
        <div class="field full"><label>Diagnóstico</label><input id="c-diag" data-mask="texto" value="${escapeHtml(c.diagnostico || '')}"></div>
        <div class="field full"><label>Observações</label><textarea id="c-obs" rows="2" data-mask="texto">${escapeHtml(c.observacoes || '')}</textarea></div>
      </div>
      <div class="inline-form"><button class="btn small" type="button" id="c-salvar-atend">Salvar atendimento</button></div>
    </div>

    <div class="section">
      <h4>Exames solicitados</h4>
      <div class="table-scroll">
        <table><thead><tr><th>#</th><th>Exame</th><th>Tipo</th><th>Status</th><th>Resultado</th></tr></thead><tbody>${examesRows}</tbody></table>
      </div>
      <div class="inline-form">
        <div class="field"><label>Novo exame</label><input id="c-exame-nome" data-mask="alfanumerico"></div>
        <div class="field"><label>Tipo</label><input id="c-exame-tipo" data-mask="alfanumerico"></div>
        <button class="btn small" type="button" id="c-exame-add">Solicitar</button>
      </div>
    </div>

    <div class="section">
      <h4>Procedimentos</h4>
      <table><thead><tr><th>#</th><th>Procedimento</th><th>Valor</th></tr></thead><tbody>${procRows}</tbody></table>
      <div class="inline-form">
        <div class="field"><label>Novo procedimento</label><input id="c-proc-nome" data-mask="alfanumerico"></div>
        <div class="field narrow"><label>Valor</label><input id="c-proc-valor" data-mask="dinheiro"></div>
        <button class="btn small" type="button" id="c-proc-add">Adicionar</button>
      </div>
    </div>

    <div class="section">${prescricaoHtml}</div>

    <div class="section">
      <h4>Pagamentos</h4>
      <div class="table-scroll">
        <table><thead><tr><th>#</th><th>Data</th><th>Valor</th><th>Forma</th><th>Situação</th><th></th></tr></thead><tbody>${pagRows}</tbody></table>
      </div>
      <div class="inline-form">
        <div class="field narrow"><label>Valor</label><input id="c-pag-valor" data-mask="dinheiro"></div>
        <div class="field"><label>Forma</label>
          <select id="c-pag-forma"><option value="PIX">PIX</option><option value="DINHEIRO">DINHEIRO</option><option value="CARTAO">CARTÃO</option><option value="CONVENIO">CONVÊNIO</option></select>
        </div>
        <button class="btn small" type="button" id="c-pag-add">Lançar pagamento</button>
      </div>
    </div>`;

  openModal(`Consulta #${c.id}`, body, `<button class="btn ghost" type="button" data-close>Fechar</button>`, true);

  const reopen = async () => { await loadState(); openConsulta({ id: c.id }); };

  document.getElementById('c-salvar-atend').onclick = async () => {
    await api('POST', `/api/consultas/${c.id}/atendimento`, {
      queixaPrincipal: document.getElementById('c-queixa').value,
      diagnostico: document.getElementById('c-diag').value,
      observacoes: document.getElementById('c-obs').value
    });
    toast('Atendimento salvo', 'success');
    await reopen();
  };
  document.getElementById('c-exame-add').onclick = async () => {
    const nome = document.getElementById('c-exame-nome').value.trim();
    if (!nome) return;
    await api('POST', `/api/consultas/${c.id}/exames`, { nome, tipo: document.getElementById('c-exame-tipo').value });
    toast('Exame solicitado', 'success');
    await reopen();
  };
  document.getElementById('c-proc-add').onclick = async () => {
    const nome = document.getElementById('c-proc-nome').value.trim();
    if (!nome) return;
    const valor = Mask.valorNumerico(document.getElementById('c-proc-valor').value) || 0;
    await api('POST', `/api/consultas/${c.id}/procedimentos`, { nome, valorPadrao: valor, valorCobrado: valor });
    toast('Procedimento adicionado', 'success');
    await reopen();
  };
  document.getElementById('c-gerar-presc')?.addEventListener('click', async () => {
    await api('POST', `/api/consultas/${c.id}/prescricao`);
    toast('Prescrição gerada', 'success');
    await reopen();
  });
  document.getElementById('c-med-add')?.addEventListener('click', async () => {
    const nome = document.getElementById('c-med-nome').value.trim();
    if (!nome) return;
    await api('POST', `/api/prescricoes/${c.prescricao.id}/medicamentos`, {
      nome,
      dose: document.getElementById('c-med-dose').value,
      frequencia: document.getElementById('c-med-freq').value,
      duracao: document.getElementById('c-med-dur').value
    });
    toast('Medicamento adicionado', 'success');
    await reopen();
  });
  document.getElementById('c-pag-add').onclick = async () => {
    const valor = Mask.valorNumerico(document.getElementById('c-pag-valor').value);
    if (!valor) return;
    await api('POST', `/api/consultas/${c.id}/pagamentos`, {
      data: c.data, valor, formaPagamento: document.getElementById('c-pag-forma').value
    });
    toast('Pagamento lançado', 'success');
    await reopen();
  };
  document.querySelectorAll('button[data-pag]').forEach(b => b.onclick = async () => {
    await api('POST', `/api/pagamentos/${b.dataset.pag}/${b.dataset.acao}`);
    toast(b.dataset.acao === 'pagar' ? 'Pagamento confirmado' : 'Pagamento estornado', 'success');
    await reopen();
  });
}

/* =========================================================================
   Inicialização
   ========================================================================= */

document.getElementById('refresh-btn').onclick = async () => {
  await loadState();
  navigate(currentView);
  toast('Dados atualizados', 'success');
};

async function init() {
  buildNav();
  try {
    await loadState();
  } catch (e) {
    document.getElementById('main').innerHTML = '<div class="card"><p class="muted">Não foi possível conectar à API. Verifique se o backend está rodando.</p></div>';
    return;
  }
  navigate('dashboard');
}

init();
