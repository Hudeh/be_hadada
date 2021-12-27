package com.hadada.service.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Component("pdfGenerator")
public class PDFGenerator {

//    @Value("${pdfDir}")
//    private static String pdfDir;
//
//    private static String logoImgPath;
//    private static Float[] logoImgScale;

    private static final Font COURIER = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
    private static final Font COURIER_SMALL = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
    private static final Font COURIER_SMALL_FOOTER = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

    public static ByteArrayOutputStream generatePdfReport(String arrayData) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, PdfPTable> tableList = new HashMap();
        int noOfColumns = 4;

        try {
            PdfWriter.getInstance(document, out);
            document.open();
//            addLogo(document);
            addDocTitle(document);
            createTable(document, noOfColumns, arrayData);
            addFooter(document);
            document.close();
            System.out.println("------------------Your PDF Report is ready!-------------------------");

        } catch (DocumentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return out;
    }

    private static void addLogo(Document document) {
//        try {
//            Image img = Image.getInstance(logoImgPath);
//            img.scalePercent(logoImgScale[0], logoImgScale[1]);
//            img.setAlignment(Element.ALIGN_RIGHT);
//            document.add(img);
//        } catch (DocumentException | IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    private static void addDocTitle(Document document) throws DocumentException {
        String localDateFormat = "dd MMMM yyyy HH:mm:ss";
        String reportFileName = "Statement Report";
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(localDateFormat));
        Paragraph p1 = new Paragraph();
        leaveEmptyLine(p1, 1);
        p1.add(new Paragraph(reportFileName, COURIER));
        p1.setAlignment(Element.ALIGN_CENTER);
        leaveEmptyLine(p1, 1);
        p1.add(new Paragraph("Report generated on " + localDateString, COURIER_SMALL));

        document.add(p1);

    }

    private static void createTable(Document document, int noOfColumns, String arrayData) throws DocumentException, IOException {
        Paragraph paragraph = new Paragraph();
        leaveEmptyLine(paragraph, 3);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(noOfColumns);
        List<String> columnNames = new ArrayList<>();
        columnNames.add("Date");
        columnNames.add("Amount");
        columnNames.add("DebitOrCredit");
        columnNames.add("Description");

        for (int i = 0; i < noOfColumns; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(columnNames.get(i)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.CYAN);
            table.addCell(cell);
        }

        JsonNode root = new ObjectMapper().readTree(arrayData);
        Iterator<JsonNode> jsonData = root.elements();
        table.setHeaderRows(1);
        getDbData(table, jsonData);
        document.add(table);
    }

    private static void getDbData(PdfPTable table, Iterator<JsonNode> jsonData) {
        String currencySymbol = "₦";

        while (jsonData.hasNext()) {
            JsonNode data = jsonData.next();

            table.setWidthPercentage(100);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

            table.addCell(data.path("transcation_date").toString());
            table.addCell(data.path("transcation_amount").toString());
            table.addCell(data.path("debitOrCredit").toString());
            table.addCell(data.path("transcation_description").toString());
        }

    }

    private static void addFooter(Document document) throws DocumentException {
        Paragraph p2 = new Paragraph();
        leaveEmptyLine(p2, 3);
        p2.setAlignment(Element.ALIGN_MIDDLE);
        p2.add(new Paragraph(
                "------------------------End Of file------------------------",
                COURIER_SMALL_FOOTER));

        document.add(p2);
    }

    private static void leaveEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}