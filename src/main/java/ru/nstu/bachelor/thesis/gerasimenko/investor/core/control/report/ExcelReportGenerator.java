package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.OperationDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.MoneyValueConverter.convert;


@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelReportGenerator {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static final String[] CLOSED_TRADES_HEADERS = new String[]{
            "Инструмент",
            "Тикер",
            "Оборот по инструменту в рамках трейда, шт",
            "Средняя цена покупки, руб/шт",
            "Средняя цена продажи, руб/шт",
            "Объём покупок, руб",
            "Объём продаж, руб",
            "Выплаты дивидендов/купонов, руб",
            "Налог, удержанный брокером, руб",
            "Комиссия, удержанная брокером, руб",
            "Доход от трейдинга (до НДФЛ), руб",
            "Прибыль от дивидендов/купонов, руб",
            "Дата открытия",
            "Дата закрытия"
    };

    public static final String[] OPEN_TRADES_HEADERS = new String[]{
            "Инструмент",
            "Тикер",
            "Количество позиций в портфеле, шт",
            "Текущая стоимость, руб",
            "Текущая цена, руб/шт",
            "Куплено в рамках трейда, шт",
            "Продано в рамках трейда, шт",
            "Средняя цена покупки, руб/шт",
            "Средняя цена продажи, руб/шт",
            "Выплаты дивидендов/купонов, руб",
            "Налог, удержанный брокером, руб",
            "Комиссия, удержанная брокером, руб",
            "Прогнозируемая комиссия брокера, руб",
            "Прогнозируемый доход от трейдинга (до НДФЛ), руб",
            "Прибыль от дивидендов/купонов, руб",
            "Дата открытия"
    };

    public static final String[] OPERATION_HEADERS = new String[]{"Дата", "Тип операции", "Количество", "Цена, руб", "Сумма, руб"};

    public static final String[] BALANCE_OPERATION_HEADERS = new String[]{"Дата", "Сумма, руб"};

    public ExcelReport generateExcelReport(Report report) {
        log.info("to generate excel report");

        try (Workbook workbook = new XSSFWorkbook()) {
            createSummarySheet(workbook, report);

            if (report.getClosedTrades() != null && !report.getClosedTrades().isEmpty()) {
                createDetailedTradesSheet(workbook, report.getClosedTrades(), "Закрытые сделки", true);
            }

            if (report.getOpenTrades() != null && !report.getOpenTrades().isEmpty()) {
                createDetailedTradesSheet(workbook, report.getOpenTrades(), "Открытые позиции", false);
            }

            createBalanceSheet(workbook, report);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            String fileName = String.format("report_%s.xlsx", LocalDateTime.now().format(DATE_FORMATTER));
            log.info("from generate excel report: fileName=[{}]", fileName);
            return new ExcelReport(fileName, outputStream.toByteArray());
        } catch (IOException e) {
            log.error("Failed to generate Excel report", e);
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private void createDetailedTradesSheet(Workbook workbook, List<TradeGroup> trades,
                                           String sheetName, boolean isClosed) {
        Map<String, List<TradeGroup>> groupedByType = trades.stream()
                .collect(Collectors.groupingBy(t -> t.getInstrument().type()));

        Sheet sheet = workbook.createSheet(sheetName);
        int rowNum = 0;

        for (Map.Entry<String, List<TradeGroup>> entry : groupedByType.entrySet()) {
            String instrumentType = entry.getKey();

            Row typeRow = sheet.createRow(rowNum++);
            Cell typeCell = typeRow.createCell(0);
            typeCell.setCellValue(instrumentType);
            typeCell.setCellStyle(createTypeHeaderStyle(workbook));
            int lastColumn = isClosed ? CLOSED_TRADES_HEADERS.length - 1 : OPEN_TRADES_HEADERS.length - 1;
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, lastColumn));

            for (TradeGroup trade : entry.getValue()) {
                Row headerRow = sheet.createRow(rowNum++);
                createHeaderRow(headerRow, isClosed ? CLOSED_TRADES_HEADERS : OPEN_TRADES_HEADERS);
                CellStyle headerStyle = createSecondHeaderStyle(workbook);
                applyHeaderStyle(headerRow, headerStyle);

                rowNum = createTradeGroup(workbook, sheet, rowNum, trade, isClosed);
                rowNum++;
            }

            rowNum++;
        }

        for (int i = 0; i < (isClosed ? CLOSED_TRADES_HEADERS.length : OPEN_TRADES_HEADERS.length); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createBalanceSheet(Workbook workbook, Report report) {
        Sheet sheet = workbook.createSheet("Операции с балансом");

        int rowNum = 0;

        int blockStartRow = rowNum;
        Row balanceHeader = sheet.createRow(rowNum++);
        balanceHeader.createCell(0).setCellValue("Операции с балансом");
        balanceHeader.getCell(0).setCellStyle(createTypeHeaderStyle(workbook));
        sheet.addMergedRegion(new CellRangeAddress(blockStartRow, blockStartRow, 0, BALANCE_OPERATION_HEADERS.length - 1));

        List<OperationDto> inputOperations = new ArrayList<>(report.getFinancialSummary().getInputOperations());
        inputOperations.sort(Comparator.comparing(OperationDto::date));

        List<OperationDto> outputOperations = new ArrayList<>(report.getFinancialSummary().getOutputOperations());
        outputOperations.sort(Comparator.comparing(OperationDto::date));

        if (!inputOperations.isEmpty()) {
            rowNum = createBalanceOperationBlock(sheet, rowNum, inputOperations, inputOperations.getFirst().operationType());
        }

        if (!outputOperations.isEmpty()) {
            rowNum = createBalanceOperationBlock(sheet, rowNum, outputOperations, outputOperations.getFirst().operationType());
        }

        for (int i = 0; i < BALANCE_OPERATION_HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }

        sheet.setColumnWidth(1, 5000);
    }

    private int createBalanceOperationBlock(Sheet sheet, int rowNum, List<OperationDto> operations, String blockTitle) {
        int blockStartRow = rowNum;
        Workbook workbook = sheet.getWorkbook();

        Row headerRow = sheet.createRow(rowNum++);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue(blockTitle);
        CellStyle headerStyle = createSecondHeaderStyle(sheet.getWorkbook());
        headerCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(blockStartRow, blockStartRow, 0, BALANCE_OPERATION_HEADERS.length - 1));

        Row opHeaderRow = sheet.createRow(rowNum++);
        for (int i = 0; i < BALANCE_OPERATION_HEADERS.length; i++) {
            Cell cell = opHeaderRow.createCell(i);
            cell.setCellValue(BALANCE_OPERATION_HEADERS[i]);
            cell.setCellStyle(createHeaderStyle(sheet.getWorkbook()));
        }

        for (OperationDto op : operations) {
            Row opRow = sheet.createRow(rowNum++);
            opRow.createCell(0).setCellValue(formatDateTime(op.date()));
            opRow.createCell(1).setCellValue(convert(Math.abs(op.payment())).doubleValue());
        }

        int blockEndRow = rowNum - 1;
        drawBorderAroundBlock(sheet, blockStartRow, blockEndRow, 0, BALANCE_OPERATION_HEADERS.length - 1, workbook);
        rowNum++;
        return rowNum;
    }

    private int createTradeGroup(Workbook workbook, Sheet sheet, int rowNum, TradeGroup trade, boolean isClosed) {
        int startRow = rowNum - 1;
        int lastColumn = isClosed ? CLOSED_TRADES_HEADERS.length - 1 : OPEN_TRADES_HEADERS.length - 1;

        Row row = sheet.createRow(rowNum++);

        row.createCell(0).setCellValue(trade.getInstrument().name());
        row.createCell(1).setCellValue(trade.getInstrument().ticker() != null ? trade.getInstrument().ticker() : "");

        if (isClosed) {
            row.createCell(2).setCellValue(trade.getTotalBuyQuantity());
            row.createCell(3).setCellValue(trade.getAvgBuyPrice().doubleValue());
            row.createCell(4).setCellValue(trade.getAvgSellPrice().doubleValue());
            row.createCell(5).setCellValue(trade.getTotalBuyValue().doubleValue());
            row.createCell(6).setCellValue(trade.getTotalSellValue().doubleValue());
            row.createCell(7).setCellValue(trade.getPassiveIncomeBeforeTax().doubleValue());
            row.createCell(8).setCellValue(trade.getAccruedTaxes().doubleValue());
            row.createCell(9).setCellValue(trade.getAccruedFees().doubleValue());
            row.createCell(10).setCellValue(trade.getProfitFromSpeculationBeforeTax().doubleValue());
            row.createCell(11).setCellValue(trade.getPassiveIncome().doubleValue());
            row.createCell(12).setCellValue(formatDateTime(trade.getOpenedAt()));
            row.createCell(13).setCellValue(formatDateTime(trade.getClosedAt()));
        } else {
            row.createCell(2).setCellValue(trade.getRemainingQuantity());
            row.createCell(3).setCellValue(trade.getCurrentAmount().doubleValue());
            row.createCell(4).setCellValue(trade.getCurrentPrice().doubleValue());
            row.createCell(5).setCellValue(trade.getTotalBuyQuantity());
            row.createCell(6).setCellValue(trade.getTotalSellQuantity());
            row.createCell(7).setCellValue(trade.getAvgBuyPrice().doubleValue());
            row.createCell(8).setCellValue(trade.getAvgSellPrice().doubleValue());
            row.createCell(9).setCellValue(trade.getPassiveIncomeBeforeTax().doubleValue());
            row.createCell(10).setCellValue(trade.getAccruedTaxes().doubleValue());
            row.createCell(11).setCellValue(trade.getAccruedFees().doubleValue());
            row.createCell(12).setCellValue(trade.getPotentialFees().doubleValue());
            row.createCell(13).setCellValue(trade.getProfitFromSpeculationBeforeTax().doubleValue());
            row.createCell(14).setCellValue(trade.getPassiveIncome().doubleValue());
            row.createCell(15).setCellValue(formatDateTime(trade.getOpenedAt()));
        }

        drawBorderForInstrumentRow(sheet, startRow + 1, 0, lastColumn, workbook);

        Row opHeaderRow = sheet.createRow(rowNum++);
        for (int i = 0; i < OPERATION_HEADERS.length; i++) {
            Cell cell = opHeaderRow.createCell(i);
            cell.setCellValue(OPERATION_HEADERS[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        for (OperationDto op : trade.getOperations()) {
            Row opRow = sheet.createRow(rowNum++);
            opRow.createCell(0).setCellValue(formatDateTime(op.date()));
            opRow.createCell(1).setCellValue(op.operationType());
            opRow.createCell(2).setCellValue(op.quantity() - op.quantityRest());
            opRow.createCell(3).setCellValue(convert(getPriceFromOperation(op)).doubleValue());
            opRow.createCell(4).setCellValue(convert(Math.abs(op.payment())).doubleValue());
        }

        int endRow = rowNum - 1;
        drawBorderAroundBlock(sheet, startRow, endRow, 0, lastColumn, workbook);

        return rowNum;
    }

    private void createSummarySheet(Workbook workbook, Report report) {
        Sheet sheet = workbook.createSheet("Итоговые показатели");
        CellStyle headerStyle = createTypeHeaderStyle(workbook);
        int rowNum = 0;

        int blockStartRow = rowNum;
        Row balanceHeader = sheet.createRow(rowNum++);
        balanceHeader.createCell(0).setCellValue("Состояние портфеля");
        balanceHeader.getCell(0).setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(blockStartRow, blockStartRow, 0, 1));

        rowNum = addSummaryRow(sheet, rowNum, "Баланс, руб",
                report.getFinancialSummary().getCurrentCashBalance().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Рыночная стоимость портфеля, руб",
                report.getFinancialSummary().getPortfolioAmount().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Потенциальная прибыль за всё время, руб",
                report.getFinancialSummary().getPotentialTotalProfit().doubleValue());

        int blockEndRow = rowNum - 1;
        drawBorderAroundSummaryBlock(sheet, blockStartRow, blockEndRow, 0, 1, workbook);

        rowNum++;

        blockStartRow = rowNum;
        Row closedHeader = sheet.createRow(rowNum++);
        closedHeader.createCell(0).setCellValue("Закрытые сделки");
        closedHeader.getCell(0).setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(blockStartRow, blockStartRow, 0, 1));

        rowNum = addSummaryRow(sheet, rowNum, "Сумма удержанных комиссий, руб",
                report.getFinancialSummary().getClosedTradesFees().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Доход от трейдов (до НДФЛ), руб",
                report.getFinancialSummary().getClosedTradesProfitBeforeTax().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "НДФЛ на доход от трейдов, руб", report.getFinancialSummary().getClosedTradesTax().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Прибыль от трейдов, руб", report.getFinancialSummary().getClosedTradesProfit().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Прибыль от дивидендов/купонов, руб", report.getFinancialSummary().getClosedTradesPassiveIncome().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Итоговая прибыль, руб", report.getFinancialSummary().getClosedTradesTotalProfit().doubleValue());

        blockEndRow = rowNum - 1;
        drawBorderAroundSummaryBlock(sheet, blockStartRow, blockEndRow, 0, 1, workbook);
        rowNum++;

        blockStartRow = rowNum;
        Row openHeader = sheet.createRow(rowNum++);
        openHeader.createCell(0).setCellValue("Открытые позиции");
        openHeader.getCell(0).setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(blockStartRow, blockStartRow, 0, 1));

        rowNum = addSummaryRow(sheet, rowNum, "Потенциальные комиссии, руб",
                report.getFinancialSummary().getOpenTradesFees().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Потенциальный доход от трейдов (до НДФЛ), руб",
                report.getFinancialSummary().getOpenTradesProfitBeforeTax().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Потенциальная налогооблагаемая база, руб",
                report.getFinancialSummary().getOpenTradesPotentialTaxableBase().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Потенциальный НДФЛ на доход от трейдов, руб",
                report.getFinancialSummary().getOpenTradesPotentialTax().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Потенциальная прибыль от трейдов, руб",
                report.getFinancialSummary().getOpenTradesPotentialProfit().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Прибыль от дивидендов/купонов, руб",
                report.getFinancialSummary().getOpenTradesPassiveIncome().doubleValue());
        rowNum = addSummaryRow(sheet, rowNum, "Потенциальная прибыль, руб",
                report.getFinancialSummary().getOpenTradesTotalPotentialProfit().doubleValue());

        blockEndRow = rowNum - 1;
        drawBorderAroundSummaryBlock(sheet, blockStartRow, blockEndRow, 0, 1, workbook);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private int addSummaryRow(Sheet sheet, int rowNum, String label, double value) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
        return rowNum;
    }

    private CellStyle createTypeHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void createHeaderRow(Row row, String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
        }
    }

    private void applyHeaderStyle(Row row, CellStyle style) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellStyle(style);
        }
    }

    private long getPriceFromOperation(OperationDto op) {
        if (op.trades() != null && !op.trades().isEmpty()) {
            return Math.abs(op.trades().getFirst().price());
        }
        return 0;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_FORMATTER);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createSecondHeaderStyle(Workbook workbook) {
        CellStyle style = createHeaderStyle(workbook);
        style.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void drawBorderAroundBlock(Sheet sheet, int startRow, int endRow, int startCol, int endCol, Workbook workbook) {
        for (int r = startRow; r <= endRow; r++) {
            Row row = sheet.getRow(r);
            for (int c = startCol; c <= endCol; c++) {
                Cell cell = row.getCell(c);
                if (cell == null) cell = row.createCell(c);

                CellStyle style = workbook.createCellStyle();
                style.cloneStyleFrom(cell.getCellStyle());

                if (r == startRow) style.setBorderTop(BorderStyle.THIN);
                if (r == endRow) style.setBorderBottom(BorderStyle.THIN);
                if (c == startCol) style.setBorderLeft(BorderStyle.THIN);
                if (c == endCol) style.setBorderRight(BorderStyle.THIN);

                cell.setCellStyle(style);
            }
        }
    }

    private void drawBorderForInstrumentRow(Sheet sheet, int rowNum, int startCol, int endCol, Workbook workbook) {
        Row row = sheet.getRow(rowNum);
        for (int c = startCol; c <= endCol; c++) {
            Cell cell = row.getCell(c);
            if (cell == null) {
                cell = row.createCell(c);
            }

            CellStyle style = workbook.createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());

            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);

            cell.setCellStyle(style);
        }
    }

    private void drawBorderAroundSummaryBlock(Sheet sheet, int startRow, int endRow, int startCol, int endCol, Workbook workbook) {
        for (int r = startRow; r <= endRow; r++) {
            Row row = sheet.getRow(r);
            for (int c = startCol; c <= endCol; c++) {
                Cell cell = row.getCell(c);
                if (cell == null) {
                    cell = row.createCell(c);
                }

                CellStyle style = workbook.createCellStyle();
                style.cloneStyleFrom(cell.getCellStyle());

                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);

                cell.setCellStyle(style);
            }
        }
    }
}