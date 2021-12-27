package com.hadada.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadada.service.modal.HadadaServiceResponse;
import com.hadada.service.modal.Transaction;
import com.hadada.service.modal.TransactionArray;
import com.hadada.service.modal.Transactions;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.io.IOException;

public class GeneratePdfReport {
    public static ByteArrayOutputStream statementReport(String pdfData) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, PdfPTable> tableList = new HashMap();

        try {
            JsonNode transactionsArray = mapper.readValue(pdfData, JsonNode.class);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Iterator<JsonNode> transactions = transactionsArray.elements();
            while(transactions.hasNext()){
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f); //Space before table
                table.setSpacingAfter(10f); //Space after table

                PdfPCell hcell;

                hcell = new PdfPCell(new Phrase("Date", headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Amount", headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(hcell);

                hcell = new PdfPCell(new Phrase("DebitOrCredit", headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Description", headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(hcell);

                JsonNode transactionObj = (JsonNode) transactions.next();
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(transactionObj.path("transcation_date").asText()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);


                cell = new PdfPCell(new Phrase(transactionObj.path("transcation_amount").asText()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(transactionObj.path("debitOrCredit").asText()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(5);
                table.addCell(cell);

                String description = transactionObj.path("transcation_description").asText();
                if(description.length() > 20) {
                    description = description.substring(0, 20);
                }

                cell = new PdfPCell(new Phrase(description));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(5);
                table.addCell(cell);
            }


            PdfWriter.getInstance(document, out);
            document.open();
            for (Map.Entry<String, PdfPTable> set : tableList.entrySet()) {
                Paragraph paragraphOne = new Paragraph("Account:"+ set.getKey(), headFont);
                document.add(paragraphOne);
                document.add(set.getValue());
            }
            document.close();

        } catch (DocumentException | IOException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }
}
