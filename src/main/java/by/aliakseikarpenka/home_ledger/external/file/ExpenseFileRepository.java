package by.aliakseikarpenka.home_ledger.external.file;

import by.aliakseikarpenka.home_ledger.domain.model.Expense;
import by.aliakseikarpenka.home_ledger.domain.value.Currency;
import by.aliakseikarpenka.home_ledger.domain.value.Money;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Service
public class ExpenseFileRepository {

    public List<Expense> getAll() {

        Path file = resolveFile();
        List<Expense> result = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file.toFile());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue; // header

                if (isRowEmpty(row)) continue;

                result.add(mapRow(row));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        Cell cell = row.getCell(0);
        return cell == null || cell.getCellType() == CellType.BLANK;
    }

    private Path resolveFile() {

        try (Stream<Path> stream = Files.list(Paths.get("."))) {

            List<Path> files = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".xlsx"))
                    .toList();

            if (files.isEmpty()) {
                throw new IllegalStateException("No XLSX file found in directory");
            }

            if (files.size() > 1) {
                throw new IllegalStateException("Multiple XLSX files found: " + files);
            }

            return files.getFirst();

        } catch (IOException e) {
            throw new RuntimeException("Cannot scan directory", e);
        }
    }

    private Expense mapRow(Row row) {

        Long id = readRequiredDecimal(row.getCell(0), "id").longValue();

        LocalDate date = row.getCell(1)
                .getLocalDateTimeCellValue()
                .toLocalDate();

        String desc = row.getCell(2).getStringCellValue();

        BigDecimal amount = readRequiredDecimal(row.getCell(3), "amount");

        Currency currency = Currency.valueOf(
                row.getCell(4).getStringCellValue().trim()
        );

        BigDecimal rate = readRequiredDecimal(row.getCell(5), "rate");

        Money money = new Money(amount, currency);

        return new Expense(id, date, desc, money, rate);
    }

    private BigDecimal readRequiredDecimal(Cell cell, String field) {

        BigDecimal value = readDecimal(cell);

        if (value == null) {
            throw new IllegalStateException("Missing required field: " + field);
        }

        return value;
    }

    private BigDecimal readDecimal(Cell cell) {

        if (cell == null) return null;

        try {
            return switch (cell.getCellType()) {

                case NUMERIC, FORMULA ->
                        BigDecimal.valueOf(cell.getNumericCellValue());

                case STRING ->
                        new BigDecimal(cell.getStringCellValue().trim());

                case BLANK ->
                        null;

                default ->
                        throw new IllegalStateException("Unsupported type: " + cell.getCellType());
            };

        } catch (Exception e) {
            throw new IllegalStateException("Invalid number in cell: " + cell, e);
        }
    }
}