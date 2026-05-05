package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.reporting;

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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.MoneyValueConverter.ONE_TO_NANO;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.OperationType.OPERATION_TYPE_BROKER_FEE;


@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelReportGenerator {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public byte[] generateExcelReport(Report report) {
        log.info("to generate excel report");

        try (Workbook workbook = new XSSFWorkbook()) {
            createSummarySheet(workbook, report);

            if (report.getClosedTrades() != null && !report.getClosedTrades().isEmpty()) {
                createDetailedTradesSheet(workbook, report.getClosedTrades(), "Закрытые сделки", true);
            }

            if (report.getOpenTrades() != null && !report.getOpenTrades().isEmpty()) {
                createDetailedTradesSheet(workbook, report.getOpenTrades(), "Открытые позиции", false);
            }

            createAllOperationsByInstrumentTypeSheet(workbook, report);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            log.info("from generate excel report");
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate Excel report", e);
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }
    /*
        private void createClosedTradesSheet(Workbook workbook, List<TradeGroup> closedTrades) {
            Sheet sheet = workbook.createSheet("Закрытые сделки");
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Инструмент", "Тикер", "Кол-во покупок", "Кол-во продаж",
                    "Средняя цена покупки (руб)", "Средняя цена продажи (руб)", "Прибыль/Убыток (руб)",
                    "Дата открытия", "Дата закрытия"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (TradeGroup trade : closedTrades) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(trade.getInstrument().name());
                row.createCell(1).setCellValue(prepareString(trade.getInstrument().ticker()));
                row.createCell(2).setCellValue(trade.getTotalBuyQuantity());
                row.createCell(3).setCellValue(trade.getTotalSellQuantity());
                row.createCell(4).setCellValue(convertToRubles(trade.getAvgBuyPrice()));
                row.createCell(5).setCellValue(convertToRubles(trade.getAvgSellPrice()));
                row.createCell(6).setCellValue(convertToRubles(trade.getProfitLoss()));
                row.createCell(7).setCellValue(formatDateTime(trade.getOpenedAt()));
                row.createCell(8).setCellValue(formatDateTime(trade.getClosedAt()));
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        private void createOpenTradesSheet(Workbook workbook, List<TradeGroup> openTrades) {
            Sheet sheet = workbook.createSheet("Открытые позиции");
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Инструмент", "Тикер", "Кол-во в портфеле", "Средняя цена покупки (руб)",
                    "Текущая цена (руб)", "Прибыль/Убыток (руб)", "Дата открытия"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (TradeGroup trade : openTrades) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(trade.getInstrument().name());
                row.createCell(1).setCellValue(prepareString(trade.getInstrument().ticker()));
                row.createCell(2).setCellValue(trade.getRemainingQuantity());
                row.createCell(3).setCellValue(convertToRubles(trade.getAvgBuyPrice()));
                row.createCell(4).setCellValue(convertToRubles(trade.getCurrentPrice()));
                row.createCell(5).setCellValue(convertToRubles(trade.getProfitLoss()));
                row.createCell(6).setCellValue(formatDateTime(trade.getOpenedAt()));
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        private void createSummarySheet(Workbook workbook, Report report) {
            Sheet sheet = workbook.createSheet("Итоги");
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Показатель");
            headerRow.createCell(1).setCellValue("Значение");
            headerRow.getCell(0).setCellStyle(headerStyle);
            headerRow.getCell(1).setCellStyle(headerStyle);

            int rowNum = 1;

            Row row1 = sheet.createRow(rowNum++);
            row1.createCell(0).setCellValue("Общая прибыль по закрытым сделкам (руб)");
            row1.createCell(1).setCellValue(convertToRubles(report.getSummary().getTotalProfit()));

            Row row2 = sheet.createRow(rowNum++);
            row2.createCell(0).setCellValue("Всего инвестировано (руб)");
            row2.createCell(1).setCellValue(convertToRubles(report.getSummary().getTotalInvested()));

            Row row3 = sheet.createRow(rowNum++);
            row3.createCell(0).setCellValue("Текущая стоимость портфеля (руб)");
            row3.createCell(1).setCellValue(convertToRubles(report.getSummary().getCurrentValue()));

            Row row4 = sheet.createRow(rowNum++);
            row4.createCell(0).setCellValue("Доходность (%)");
            row4.createCell(1).setCellValue(report.getSummary().getTotalReturnPercent().doubleValue());

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
        }
    */
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
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8));

            Row headerRow = sheet.createRow(rowNum++);
            createHeaderRow(headerRow, isClosed ? getTradeHeaders() : getOpenTradeHeaders());
            CellStyle headerStyle = createHeaderStyle(workbook);
            applyHeaderStyle(headerRow, headerStyle);

            for (TradeGroup trade : entry.getValue()) {
                rowNum = createTradeRow(sheet, rowNum, trade, isClosed);
                rowNum++;
            }

            rowNum++;
        }

        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createAllOperationsByInstrumentTypeSheet(Workbook workbook, Report report) {
        List<TradeGroup> allTrades = new ArrayList<>();

        if (report.getClosedTrades() != null) {
            allTrades.addAll(report.getClosedTrades());
        }

        if (report.getOpenTrades() != null) {
            allTrades.addAll(report.getOpenTrades());
        }

        Map<String, List<TradeGroup>> groupedByType = allTrades.stream()
                .collect(Collectors.groupingBy(t -> t.getInstrument().type()));

        Sheet sheet = workbook.createSheet("Все операции по инструментам");
        int rowNum = 0;

        for (Map.Entry<String, List<TradeGroup>> entry : groupedByType.entrySet()) {
            String instrumentType = entry.getKey();

            Row typeRow = sheet.createRow(rowNum++);
            Cell typeCell = typeRow.createCell(0);
            typeCell.setCellValue(instrumentType);
            typeCell.setCellStyle(createTypeHeaderStyle(workbook));
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 12));

            for (TradeGroup trade : entry.getValue()) {
                Row instrumentRow = sheet.createRow(rowNum++);
                Cell instrumentCell = instrumentRow.createCell(0);
                instrumentCell.setCellValue(String.format("%s (%s)",
                        trade.getInstrument().name(),
                        trade.getInstrument().ticker() != null ? trade.getInstrument().ticker() : ""));
                instrumentCell.setCellStyle(createInstrumentHeaderStyle(workbook));
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 12));

                Row opHeaderRow = sheet.createRow(rowNum++);
                String[] opHeaders = {"Дата", "Тип операции", "Количество", "Цена (руб)",
                        "Сумма (руб)", "Комиссия (руб)", "Статус"};
                for (int i = 0; i < opHeaders.length; i++) {
                    Cell cell = opHeaderRow.createCell(i);
                    cell.setCellValue(opHeaders[i]);
                    cell.setCellStyle(createHeaderStyle(workbook));
                }

                for (OperationDto op : trade.getOperations()) {
                    Row opRow = sheet.createRow(rowNum++);
                    opRow.createCell(0).setCellValue(formatDateTime(op.date()));
                    opRow.createCell(1).setCellValue(op.operationType());
                    opRow.createCell(2).setCellValue(op.quantity());
                    opRow.createCell(3).setCellValue(convertToRubles(getPriceFromOperation(op)));
                    opRow.createCell(4).setCellValue(convertToRubles(Math.abs(op.payment())));
                    opRow.createCell(5).setCellValue(convertToRubles(getCommissionFromOperation(op)));
                    opRow.createCell(6).setCellValue(op.operationState());
                }

                Row summaryRow = sheet.createRow(rowNum++);
                CellStyle summaryStyle = createSummaryStyle(workbook);
                summaryRow.createCell(0).setCellValue("Итого:");
                summaryRow.getCell(0).setCellStyle(summaryStyle);
                summaryRow.createCell(4).setCellValue(convertToRubles(Math.abs(trade.getProfitLoss())));
                summaryRow.getCell(4).setCellStyle(summaryStyle);

                if (trade.getOtherOperationsSum() != null && !trade.getOtherOperationsSum().isEmpty()) {
                    rowNum = createOtherOperationsRow(sheet, rowNum, trade.getOtherOperationsSum());
                }

                rowNum++;
            }
        }

        for (int i = 0; i < 13; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private int createOtherOperationsRow(Sheet sheet, int rowNum, Map<String, Long> otherOps) {
        Row otherRow = sheet.createRow(rowNum++);
        otherRow.createCell(0).setCellValue("Прочие операции:");

        int col = 1;
        for (Map.Entry<String, Long> entry : otherOps.entrySet()) {
            otherRow.createCell(col++).setCellValue(String.format("%s: %s", entry.getKey(), convertToRubles(entry.getValue())));
        }
        return rowNum;
    }

    private int createTradeRow(Sheet sheet, int rowNum, TradeGroup trade, boolean isClosed) {
        Row row = sheet.createRow(rowNum++);

        row.createCell(0).setCellValue(trade.getInstrument().name());
        row.createCell(1).setCellValue(trade.getInstrument().ticker() != null ? trade.getInstrument().ticker() : "");
        row.createCell(2).setCellValue(trade.getTotalBuyQuantity());
        row.createCell(3).setCellValue(trade.getTotalSellQuantity());
        row.createCell(4).setCellValue(convertToRubles(trade.getAvgBuyPrice()));
        row.createCell(5).setCellValue(convertToRubles(trade.getAvgSellPrice()));
        row.createCell(6).setCellValue(convertToRubles(trade.getProfitLoss()));
        row.createCell(7).setCellValue(formatDateTime(trade.getOpenedAt()));
        if (isClosed) {
            row.createCell(8).setCellValue(formatDateTime(trade.getClosedAt()));
        }

        return rowNum;
    }

    private void createSummarySheet(Workbook workbook, Report report) {
        Sheet sheet = workbook.createSheet("Итоговая сводка");
        CellStyle headerStyle = createHeaderStyle(workbook);
        int rowNum = 0;

        Row closedHeader = sheet.createRow(rowNum++);
        closedHeader.createCell(0).setCellValue("Закрытые сделки");
        closedHeader.getCell(0).setCellStyle(createTypeHeaderStyle(workbook));

        rowNum = addSummaryRow(sheet, rowNum, "Общая прибыль (руб)",
                convertToRubles(report.getSummary().getTotalProfit()));
        rowNum = addSummaryRow(sheet, rowNum, "Всего инвестировано (руб)",
                convertToRubles(report.getSummary().getTotalInvested()));

        rowNum++;

        Row openHeader = sheet.createRow(rowNum++);
        openHeader.createCell(0).setCellValue("Открытые позиции");
        openHeader.getCell(0).setCellStyle(createTypeHeaderStyle(workbook));

        rowNum = addSummaryRow(sheet, rowNum, "Текущая стоимость портфеля (руб)",
                convertToRubles(report.getSummary().getCurrentValue()));

        rowNum++;

        Row totalHeader = sheet.createRow(rowNum++);
        totalHeader.createCell(0).setCellValue("Общая доходность");
        totalHeader.getCell(0).setCellStyle(createTypeHeaderStyle(workbook));

        rowNum = addSummaryRow(sheet, rowNum, "Общая доходность (%)",
                report.getSummary().getTotalReturnPercent().doubleValue());

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
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createInstrumentHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createSummaryStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
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

    private String[] getTradeHeaders() {
        return new String[]{"Инструмент", "Тикер", "Кол-во покупок", "Кол-во продаж",
                "Ср. цена покупки (руб)", "Ср. цена продажи (руб)", "Прибыль/Убыток (руб)",
                "Дата открытия", "Дата закрытия"};
    }

    private String[] getOpenTradeHeaders() {
        return new String[]{"Инструмент", "Тикер", "Кол-во в портфеле", "Ср. цена покупки (руб)",
                "Текущая цена (руб)", "Прибыль/Убыток (руб)", "Дата открытия"};
    }

    private long getPriceFromOperation(OperationDto op) {
        if (op.trades() != null && !op.trades().isEmpty()) {
            return op.trades().getFirst().price();
        }
        return op.quantity() > 0 ? Math.abs(op.payment()) / op.quantity() : 0;
    }

    private long getCommissionFromOperation(OperationDto op) {
        if (OPERATION_TYPE_BROKER_FEE.getType().equals(op.operationType())) {
            return Math.abs(op.payment());
        }
        return 0;
    }

    private double convertToRubles(long nanoValue) {
        return BigDecimal.valueOf(nanoValue)
                .divide(BigDecimal.valueOf(ONE_TO_NANO), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
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
}