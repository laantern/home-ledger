<template>
  <div class="app">
    <header class="header">
      <h1>HomeLedger</h1>
      <p>Учёт расходов</p>
    </header>

    <div class="container">
      <div v-if="loading" class="state">Загрузка...</div>

      <div v-if="error" class="state error">
        {{ error }}
      </div>

      <div v-if="!loading && expenses.length">

        <div class="summary">
          <div class="summary-item">
            Записей: <span>{{ expenses.length }}</span>
          </div>

          <div class="summary-item">
            Итого BYN: <span>{{ totalByn }}</span>
          </div>
        </div>

        <table class="table">
          <thead>
            <tr>
              <th class="col-date clickable" @click="changeSort('date')">
                Дата
                <span v-if="sortKey === 'date'">
                  {{ sortDir === 'asc' ? '↑' : '↓' }}
                </span>
              </th>

              <th class="col-text">
                Описание
              </th>

              <th class="col-num">
                Сумма
              </th>

              <th class="col-num clickable" @click="changeSort('amountByn')">
                BYN
                <span v-if="sortKey === 'amountByn'">
                  {{ sortDir === 'asc' ? '↑' : '↓' }}
                </span>
              </th>

              <th class="col-num clickable" @click="changeSort('amountUsd')">
                USD
                <span v-if="sortKey === 'amountUsd'">
                  {{ sortDir === 'asc' ? '↑' : '↓' }}
                </span>
              </th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="e in sortedExpenses" :key="e.id">
              <td class="col-date">{{ formatDate(e.date) }}</td>

              <td class="col-text">
                {{ e.description }}
              </td>

              <td class="col-num">
                <span class="tooltip" :data-rate="e.rateToBynOnDate">
                  {{ formatMoney(e.amount, e.currency) }}
                </span>
              </td>

              <td class="col-num">
                <span class="tooltip" :data-rate="e.rateToBynOnDate">
                  {{ formatMoney(e.amountByn, 'BYN') }}
                </span>
              </td>

              <td class="col-num">
                <span class="tooltip" :data-rate="e.rateToBynOnDate">
                  {{ formatMoney(e.amountUsd, 'USD') }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>

      </div>

      <div v-if="!loading && !expenses.length" class="state empty">
        Расходов нет<br />
        <span>Добавь Excel файл и обнови страницу</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import axios from 'axios'

const expenses = ref([])
const loading = ref(true)
const error = ref(null)

/** SORT STATE */
const sortKey = ref('date')
const sortDir = ref('desc')

async function loadExpenses() {
  try {
    loading.value = true
    const res = await axios.get('/api/expenses')
    expenses.value = res.data
  } catch (e) {
    error.value = 'Не удалось загрузить данные'
  } finally {
    loading.value = false
  }
}

/** SORT */
const sortedExpenses = computed(() => {
  const data = [...expenses.value]

  return data.sort((a, b) => {
    const key = sortKey.value
    const dir = sortDir.value === 'asc' ? 1 : -1

    let aVal = a[key]
    let bVal = b[key]

    if (key === 'date') {
      aVal = new Date(aVal).getTime()
      bVal = new Date(bVal).getTime()
    }

    if (aVal == null) return 1
    if (bVal == null) return -1

    return (aVal - bVal) * dir
  })
})

function changeSort(key) {
  if (sortKey.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDir.value = 'asc'
  }
}

/** FORMATTERS */
function formatMoney(amount, currency) {
  if (amount == null) return '-'

  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency
  }).format(amount)
}

function formatDate(date) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('ru-RU')
}

/** TOTAL */
const totalByn = computed(() => {
  return expenses.value
    .reduce((sum, e) => sum + (e.amountByn || 0), 0)
    .toFixed(2)
})

onMounted(loadExpenses)
</script>

<style>
:root {
  --bg: #0f1115;
  --panel: #151922;
  --panel-2: #1b2030;
  --text: #e6e6e6;
  --muted: #8b90a0;
  --border: #232838;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: Inter, Arial, sans-serif;
  background: var(--bg);
  color: var(--text);
}

/* HEADER */
.header {
  padding: 24px 32px;
  border-bottom: 1px solid var(--border);
}

.header h1 {
  margin: 0;
  font-size: 22px;
}

.header p {
  margin: 4px 0 0;
  color: var(--muted);
  font-size: 13px;
}

/* CONTAINER */
.container {
  padding: 24px 32px;
}

/* SUMMARY */
.summary {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 12px;
}

.summary-item {
  color: var(--muted);
  font-size: 13px;
}

.summary-item span {
  color: var(--text);
  font-weight: 500;
}

/* TABLE */
.table {
  width: 100%;
  border-collapse: collapse;
  background: var(--panel);
  border-radius: 12px;
  overflow: hidden;
  table-layout: fixed; /* 🔥 фиксированная сетка */
}

/* HEADER */
.table thead th {
  position: sticky;
  top: 0;
  background: var(--panel-2);
  z-index: 10;
  color: var(--muted);
  font-weight: 500;
  border-bottom: 1px solid var(--border);
  text-align: left; /* 🔥 важно */
}

/* CELLS */
.table th,
.table td {
  padding: 12px 14px;
  font-size: 13px;
  border-bottom: 1px solid var(--border);
  vertical-align: middle;
  text-align: left;
}

.table td {
  line-height: 1.3;
}

/* ALIGNMENT */
.col-date {
  width: 120px;
  text-align: left;
}

.col-text {
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.col-num {
  text-align: right;
  font-variant-numeric: tabular-nums;
  width: 140px;
}

/* ZEBRA */
.table tbody tr:nth-child(even) {
  background: rgba(255, 255, 255, 0.02);
}

/* HOVER */
.table tbody tr:hover {
  background: var(--panel-2);
}

.clickable {
  cursor: pointer;
  user-select: none;
}

.clickable:hover {
  color: var(--text);
  opacity: 0.85;
}

/* TOOLTIP */
.tooltip {
  position: relative;
  cursor: help;
}

.tooltip:hover::after {
  content: "Курс: " attr(data-rate);
  position: absolute;
  right: 0;
  top: -26px;
  background: #000;
  padding: 4px 8px;
  font-size: 11px;
  border-radius: 6px;
  white-space: nowrap;
  color: #fff;
  z-index: 20;
}

/* STATES */
.state {
  color: var(--muted);
  padding: 20px;
}

.state.error {
  color: #ff6b6b;
}

.state.empty {
  text-align: center;
  padding: 60px 20px;
}
</style>