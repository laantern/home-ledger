package by.aliakseikarpenka.home_ledger.usecase.get_all.external;

import by.aliakseikarpenka.home_ledger.domain.model.Expense;
import by.aliakseikarpenka.home_ledger.external.file.ExpenseFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpensesProvider {

    private final ExpenseFileRepository fileRepository;

    public ExpensesProvider(ExpenseFileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<Expense> getExpenses() {
        return fileRepository.getAll();
    }

}
