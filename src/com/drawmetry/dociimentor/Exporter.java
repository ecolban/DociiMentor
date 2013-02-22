package com.drawmetry.dociimentor;

import com.drawmetry.dociimentor.database.DataAccessObject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * This class is used to export the entries shown in the UI to an Excel file.
 *
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class Exporter {

    private static final int HEADING_ROW = 0;
    private static final int DOC_ID_COL = 0;
    private static final int GROUP_COL = 1;
    private static final int TITLE_COL = 2;
    private static final int TIME_COL = 3;
    private static final int AUTHORS_COL = 4;
    private static final int NOTES_COL = 5;
    private static final ResourceBundle messageBundle =
            ResourceBundle.getBundle("com/drawmetry/dociimentor/resources/MessageBundle");
    private static final String DOCUMENT = messageBundle.getString("DOCUMENT ID");
    private static final String DOC_GROUP = messageBundle.getString("DOCUMENT GROUP");
    private static final String TITLE = messageBundle.getString("TITLE");
    private static final String UPLOAD_TIME = messageBundle.getString("UPLOAD TIME");
    private static final String AUTHORS = messageBundle.getString("AUTHORS");
    private static final String NOTES = messageBundle.getString("NOTES");
    private Workbook wb;
    private DocEntry[] entries;
    private String table;
//    private final UI ui;
    private final DataAccessObject db;
    private final Map<String, String> groupCodeToNameMap;

    /**
     * Constructor 
     * @param db the database where the entries can be looked up.
     */
    public Exporter(UI ui) {
//        this.ui = ui;
        this.db = ui.getDb();
        this.table = ui.getTable();
        this.entries = ui.getEntries();
        this.groupCodeToNameMap = ui.getWorkingGroup().getGroupCodeToNameMap();
    }

    /**
     * Writes the entries to an Excel file
     * @param fileOut the output stream
     * 
     * @throws IOException if cannot write to fileOut
     */
    public void write(FileOutputStream fileOut) throws IOException {
        this.wb = new HSSFWorkbook();
        insertDocsSheet();
        if (fileOut != null) {
            wb.write(fileOut);
        } else {
            throw new IOException("fileOut is null");
        }
    }

    private void insertDocsSheet() {
        Sheet sheet1 = wb.createSheet("docs");
        // create the heading row
        insertHeadingRow(sheet1);
        insertDataRows(sheet1, HEADING_ROW + 1);

        //Set the column widths
        sheet1.setColumnWidth(DOC_ID_COL, 27 * 256);
        sheet1.setColumnWidth(GROUP_COL, 16 * 256);
        sheet1.setColumnWidth(TITLE_COL, 40 * 256);
        sheet1.setColumnWidth(TIME_COL, 20 * 256);
        sheet1.setColumnWidth(AUTHORS_COL, 30 * 256);
        sheet1.setColumnWidth(NOTES_COL, 70 * 256);
        sheet1.createFreezePane(DOC_ID_COL, HEADING_ROW + 1);
    }

    private void insertHeadingRow(Sheet sheet1) {
        CellStyle headingStyle = createHeadingStyle();
        Row row = sheet1.createRow(HEADING_ROW);
        Cell documentHeading = row.createCell(DOC_ID_COL);
        documentHeading.setCellValue(DOCUMENT);
        documentHeading.setCellStyle(headingStyle);
        Cell groupHeading = row.createCell(GROUP_COL);
        groupHeading.setCellValue(DOC_GROUP);
        groupHeading.setCellStyle(headingStyle);
        Cell titelHeading = row.createCell(TITLE_COL);
        titelHeading.setCellValue(TITLE);
        titelHeading.setCellStyle(headingStyle);
        Cell timeHeading = row.createCell(TIME_COL);
        timeHeading.setCellValue(UPLOAD_TIME);
        timeHeading.setCellStyle(headingStyle);
        Cell authorsHeading = row.createCell(AUTHORS_COL);
        authorsHeading.setCellValue(AUTHORS);
        authorsHeading.setCellStyle(headingStyle);
        Cell notesHeading = row.createCell(NOTES_COL);
        notesHeading.setCellValue(NOTES);
        notesHeading.setCellStyle(headingStyle);
    }

    private void insertDataRows(Sheet sheet1, int startRow) {
        // Create the autowrap style
        CellStyle autowrapStyle = createAutowrapStyle();
        // create the hlink style
        CellStyle hlinkStyle = createHlinkStyle();
        // Data rows
        CreationHelper createHelper = wb.getCreationHelper();
        for (int i = 0, length = entries.length; i < length; i++) {
            Row row = sheet1.createRow(i + startRow);
            DocumentObject docObj = db.getDocumentOject(table, entries[i].getId());
            Cell filePrefixCell = row.createCell(DOC_ID_COL);
            filePrefixCell.setCellValue(docObj.getDocumentId());
            Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
            link.setAddress(docObj.getURL().toString());
            filePrefixCell.setHyperlink(link);
            filePrefixCell.setCellStyle(hlinkStyle);
            Cell groupCell = row.createCell(GROUP_COL);
            String groupName = groupCodeToNameMap.get(docObj.getGroupCode());
            groupCell.setCellValue(groupName);
            groupCell.setCellStyle(autowrapStyle);
            Cell titleCell = row.createCell(TITLE_COL);
            titleCell.setCellValue(docObj.getTitle());
            titleCell.setCellStyle(autowrapStyle);
            Cell timeCell = row.createCell(TIME_COL);
            timeCell.setCellValue(docObj.getUploadDateString());
            timeCell.setCellStyle(autowrapStyle);
            Cell authorsCell = row.createCell(AUTHORS_COL);
            authorsCell.setCellValue(docObj.getAuthors());
            authorsCell.setCellStyle(autowrapStyle);
            Cell notesCell = row.createCell(NOTES_COL);
            notesCell.setCellValue(docObj.getNotes());
            notesCell.setCellStyle(autowrapStyle);
        }
    }

    private CellStyle createHeadingStyle() {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Arial");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createAutowrapStyle() {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createHlinkStyle() {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        return style;
    }

    
}
