/* =========================================================================
   Utilitarios compartilhados por index.html e crud-generico.html.
   Nao dependem de nenhuma tela: podem ser usados por qualquer pagina.
   ========================================================================= */

/* ---------- Mascaras (fallback caso /js/masks.js nao seja carregado) ---------- */

const Mask = window.Mascaras || {
  atributos: () => '',
  valorNumerico: v => { const n = Number(String(v ?? '').replace(/\./g, '').replace(',', '.')); return isNaN(n) ? NaN : n; },
  formatarMoeda: v => (v === null || v === undefined || v === '' ? '' : String(v))
};

/* ---------- Acesso a valores aninhados e exibicao segura ---------- */

function getPath(obj, path) {
  return path.split('.').reduce((acc, part) => (acc == null ? undefined : acc[part]), obj);
}
function setPath(obj, path, value) {
  const parts = path.split('.');
  let cur = obj;
  for (let i = 0; i < parts.length - 1; i++) { cur[parts[i]] = cur[parts[i]] || {}; cur = cur[parts[i]]; }
  cur[parts[parts.length - 1]] = value;
}
/** Monta "#12 · Cardiologia" / "#12" / "—" para colunas que apontam para outro registro. */
function referencia(id, complemento) {
  if (id == null || id === '') return '—';
  return complemento ? `#${id} · ${complemento}` : `#${id}`;
}
function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;').replaceAll("'", '&#39;');
}
