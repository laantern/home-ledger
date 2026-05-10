package by.aliakseikarpenka.home_ledger.usecase.get_all.service;

import by.aliakseikarpenka.home_ledger.domain.model.Expense;
import by.aliakseikarpenka.home_ledger.usecase.get_all.external.ExpensesProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpensesProvider expensesProvider;

    public ExpenseService(ExpensesProvider expensesProvider) {
        this.expensesProvider = expensesProvider;
    }

    public List<Expense> getAllExpenses() {
        return expensesProvider.getExpenses();
    }

}
