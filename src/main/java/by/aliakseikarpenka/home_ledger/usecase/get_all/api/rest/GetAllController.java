package by.aliakseikarpenka.home_ledger.usecase.get_all.api.rest;

import by.aliakseikarpenka.home_ledger.usecase.get_all.api.rest.dto.ExpenseDto;
import by.aliakseikarpenka.home_ledger.usecase.get_all.service.ExpenseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class GetAllController {

    private final ExpenseService expenseService;

    public GetAllController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseDto> getAllExpenses() {
        return expenseService.getAllExpenses().stream().map( expense ->
                new ExpenseDto(
                        expense.getId(),
                        expense.getDate(),
                        expense.getDescription(),
                        expense.getAmount().amount(),
                        expense.getAmount().currency().name(),
                        expense.getBYNAmount().amount(),
                        expense.getUSDAmount().amount(),
                        expense.getRateToBynOnDate()
                ))
        .toList();
    }
}
