package com.qa.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel read/write helper built on Apache POI (.xlsx).
 */
public class ExcelUtils {

    /**
     * Step 6: Create an excel file with a list of expected station names in one column.
     */
    public static void writeSingleColumn(String filePath, String sheetName,
                                         String header, List<String> values) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue(header);
            for (int i = 0; i < values.size(); i++) {
                sheet.createRow(i + 1).createCell(0).setCellValue(values.get(i));
            }
            sheet.autoSizeColumn(0);
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Read a single column (col 0) from a sheet, skipping the header row.
     */
    public static List<String> readSingleColumn(String filePath, String sheetName) throws IOException {
        List<String> values = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null) {
                    String v = row.getCell(0).getStringCellValue().trim();
                    if (!v.isEmpty()) values.add(v);
                }
            }
        }
        return values;
    }

    /**
     * Step 7: Write actual dropdown data + comparison result (Expected vs Actual) side by side.
     */
    public static void writeComparison(String filePath, String sheetName,
                                       List<String> expected, List<String> actual) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Expected Station");
            headerRow.createCell(1).setCellValue("Actual (from dropdown)");
            headerRow.createCell(2).setCellValue("Match?");

            int rows = Math.max(expected.size(), actual.size());
            for (int i = 0; i < rows; i++) {
                Row row = sheet.createRow(i + 1);
                String exp = i < expected.size() ? expected.get(i) : "";
                String act = i < actual.size() ? actual.get(i) : "";
                row.createCell(0).setCellValue(exp);
                row.createCell(1).setCellValue(act);
                // "match" = expected station appears anywhere in actual dropdown list
                boolean match = !exp.isEmpty() && actual.stream()
                        .anyMatch(a -> a.toLowerCase().contains(exp.toLowerCase()));
                row.createCell(2).setCellValue(exp.isEmpty() ? "" : (match ? "YES" : "NO"));
            }
            for (int c = 0; c <= 2; c++) sheet.autoSizeColumn(c);

            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Use Case 2: read login data (username, password, expectedResult) for the DataProvider.
     * Returns Object[][] suitable for TestNG.
     */
    public static Object[][] readLoginData(String filePath, String sheetName) throws IOException {
        List<Object[]> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String username = formatter.formatCellValue(row.getCell(0)).trim();
                String password = formatter.formatCellValue(row.getCell(1)).trim();
                String expected = formatter.formatCellValue(row.getCell(2)).trim();
                data.add(new Object[]{username, password, expected});
            }
        }
        return data.toArray(new Object[0][]);
    }
}
