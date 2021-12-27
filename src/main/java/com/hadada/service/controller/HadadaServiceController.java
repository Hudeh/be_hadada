package com.hadada.service.controller;

import com.hadada.service.dto.LenderDetails;
import com.hadada.service.dto.WidgetUsersDto;
import com.hadada.service.encrypt.EncryptDecrypt;
import com.hadada.service.modal.*;
import com.hadada.service.repositories.CollectedPdfRepository;
import com.hadada.service.repositories.CustomerRepository;
import com.hadada.service.repositories.StatementRepository;
import com.hadada.service.services.*;
import com.hadada.service.util.GeneratePdfReport;
import com.hadada.service.util.PDFGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("hadada-service")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HadadaServiceController {

    @Autowired
    HadadaService hadadaService;

    @Autowired
    EmailService emailService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CollectedPdfRepository collectedPdfRepository;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private WidgetUsersService widgetUsersService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LenderService lenderService;

    @Autowired
    private RunTaskScheduleService runTaskScheduleService;

    @GetMapping("/test-check")
    public String testCheck() {
        return "Service is healthy.";
    }

    @GetMapping("/get-lender-details/{authToken}")
    public LenderDetails getLenderDetails(@PathVariable final String authToken) {
        return lenderService.getLenderDetails(authToken);
    }

    @PostMapping("/balance-api")
    public HadadaServiceResponse getBalanceApi(@Validated @RequestBody WidgetRequest widgetRequest) {
        return hadadaService.getBalanceAndTransactionApi(widgetRequest, "balance");
    }

    @PostMapping("/transaction-api")
    public HadadaServiceResponse getTransactionApi(@Validated @RequestBody WidgetRequest widgetRequest) {
        return hadadaService.getBalanceAndTransactionApi(widgetRequest, "transaction");
    }

    @GetMapping("/get-statement/{sessionKey}")
    public Statement getStatement(@PathVariable String sessionKey) {
        Statement statement = statementRepository.findBySessionKey(sessionKey);
        Customer customer = customerRepository.findByUsername(statement.getEmail());
        statement.setAuthKey(EncryptDecrypt.decryptKey(customer.getAuthKey()));
        return statement;
    }

    @PostMapping("/save/widget/email")
    public ResponseEntity<WidgetUsersDto> saveWidgetUserDto(@RequestBody final WidgetUsersDto usersDto) {
        return ResponseEntity.ok().body(widgetUsersService.saveWidgetUserEmail(usersDto));
    }
//
//    @PostMapping("/create/statement")
//    public ResponseEntity<WidgetStatementResponse> createBankStatement(@Validated @RequestBody WidgetRequest widgetRequest) {
//        System.out.println("widgetRequest" + widgetRequest);
//        WidgetStatementResponse response = hadadaService.saveStatementApi(widgetRequest, "transaction");
//
//        return ResponseEntity.ok().body(response);
//    }

    @PostMapping(path = "/download-statement",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity downloadStatement(@Validated @RequestBody DownloadStatementRequest downloadStatementRequest) {
        HadadaServiceResponse hadadaServiceResponse = hadadaService.getStatementApi(downloadStatementRequest, "transaction");
        HttpHeaders headers = new HttpHeaders();

        if (hadadaServiceResponse.getData().equalsIgnoreCase("")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found.");
        }

        ByteArrayOutputStream bos = PDFGenerator.generatePdfReport(hadadaServiceResponse.getData());
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

        headers.add("Content-Disposition", "inline; filename=transactionreport.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        CollectedPDF collectedPDF = new CollectedPDF();
        collectedPDF.setPdfData(hadadaServiceResponse.getData());
        collectedPDF.setCustomerName(downloadStatementRequest.getUsername());
        collectedPDF.setSessionKey(downloadStatementRequest.getAppKey());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        collectedPDF.setCollectedDate(dateFormat.format(date));
        collectedPdfRepository.save(collectedPDF);

        String toEmail = downloadStatementRequest.getOfficialEmail().isEmpty() ?
                hadadaServiceResponse.getEmail() : downloadStatementRequest.getOfficialEmail();

        emailService.sendMailWithAttachment(toEmail, "Bank Statement", "Below is the bank statement", bos);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping(path = "/download-pdf/{collectedPdfId}",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity downloadPdf(@PathVariable Long collectedPdfId) {
        Optional<CollectedPDF> collectedPDFOptional = collectedPdfRepository.findById(collectedPdfId);
        if (!collectedPDFOptional.isPresent()) {
            return new ResponseEntity<>("Pdf Not found", HttpStatus.NOT_FOUND);
        }
        CollectedPDF collectedPDF = collectedPDFOptional.get();
        HttpHeaders headers = new HttpHeaders();

        ByteArrayOutputStream bos = GeneratePdfReport.statementReport(collectedPDF.getPdfData());

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

        headers.add("Content-Disposition", "inline; filename=transactionreport.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
