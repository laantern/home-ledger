import axios from 'axios'

export async function getExpenses() {
    const response = await axios.get('/api/expenses')
    return response.data
}

export async function createExpense(payload) {
    await axios.post('/api/expenses', payload)
}