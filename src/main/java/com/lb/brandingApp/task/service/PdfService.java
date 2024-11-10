package com.lb.brandingApp.task.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.text.DocumentException;
import com.lb.brandingApp.category.data.models.response.PageResponseDto;
import com.lb.brandingApp.task.data.models.request.ReportRequestDto;
import com.lb.brandingApp.task.data.models.response.TaskResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PdfService {

    DateTimeFormatter dateFormatter;

    public PdfService() {
        dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public void generatePdf(PageResponseDto<TaskResponseDto> report, ByteArrayOutputStream byteArrayOutputStream,
                            LocalDate fromDate, LocalDate toDate, ReportRequestDto request)
            throws DocumentException, IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(byteArrayOutputStream));
        Document document = new Document(pdfDoc);
        document.setMargins(2, 2, 2, 2);
        PdfFont paraFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        if (report.getContent().isEmpty()) {
            document.add(new Paragraph("This document is empty.").setFont(paraFont)
                    .setFontColor(ColorConstants.ORANGE).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.close();
            return;
        }
        document.add(new Paragraph(report.getContent().get(0).getCategory().getName()).setFont(paraFont)
                .setFontColor(ColorConstants.ORANGE).setFontSize(24).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(dateFormatter.format(toDate) + " - " + dateFormatter.format(fromDate))
                .setFont(paraFont).setFontColor(ColorConstants.GREEN).setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER));
        Table table = new Table(request.configs().size()).useAllAvailableWidth();
        table.setMarginTop(10);
        table.setMarginBottom(5);
        addTableHeader(table, request.configs());
        addRows(table, report, request.configs());
        document.add(table);
        document.close();
    }

    private void addTableHeader(Table table, List<String> configs) throws IOException {
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        for (String headerCol : configs) {
            Cell cell = new Cell(1, 1)
                    .add(new Paragraph(headerCol))
                    .setFont(headerFont)
                    .setFontSize(12)
                    .setFontColor(ColorConstants.BLACK)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(cell);
        }
    }

    private void addRows(Table table, PageResponseDto<TaskResponseDto> report, List<String> configs) throws IOException {
        PdfFont rowFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        for (TaskResponseDto reportItem : report.getContent()) {
            for (String config : configs) {
                switch (config) {
                    case "District" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(reportItem.getDistrict().getName()))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Location" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(reportItem.getLocation()))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Name", "Dealer Name", "Owner Name", "Agency Name", "Demanded By",
                            "Name/Area", "Painter Name", "Festive Name" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(reportItem.getName()))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Mobile Number" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(reportItem.getMobileNumber()))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Verified On" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(
                                    Objects.nonNull(reportItem.getVerifiedAt()) ?
                                            dateFormatter.format(reportItem.getVerifiedAt()) : "-"))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Verified By" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(Objects.nonNull(reportItem.getVerifiedBy()) ?
                                    reportItem.getVerifiedBy().getName() : "-"))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Item Details", "Items" -> {
                        Table table1 = new Table(1).useAllAvailableWidth();
                        table1.setMarginTop(5);
                        table1.setMarginBottom(5);
                        reportItem.getAllotments().forEach(allotmentResponseDto ->
                                table1.addCell(new Paragraph(allotmentResponseDto.getProduct().getName() + " - "
                                        + allotmentResponseDto.getDimension().getLength() + " * "
                                        + allotmentResponseDto.getDimension().getWidth() + " - "
                                        + allotmentResponseDto.getQuantity().getValue())));
                        table.addCell(new Cell(1, 1)
                                        .add(table1))
                                .setFont(rowFont)
                                .setFontSize(12)
                                .setFontColor(ColorConstants.BLACK)
                                .setTextAlignment(TextAlignment.CENTER);
                    }
                    case "Size" -> {
                        Paragraph paragraph = new Paragraph();
                        reportItem.getAllotments().forEach(allotmentResponseDto ->
                                paragraph.add(
                                        allotmentResponseDto.getDimension().getLength() + " * "
                                                + allotmentResponseDto.getDimension().getWidth() + " - "));
                        table.addCell(new Cell(1, 1)
                                        .add(paragraph))
                                .setFont(rowFont)
                                .setFontSize(12)
                                .setFontColor(ColorConstants.BLACK)
                                .setTextAlignment(TextAlignment.CENTER);
                    }
                    case "Area" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(String.valueOf(reportItem.getArea().getValue())))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Amount", "Cash" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(String.valueOf(reportItem.getAmount().getValue())))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Rent" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(Objects.nonNull(reportItem.getRent()) ?
                                    String.valueOf(reportItem.getRent().getValue()) : "-"))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Installed On" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(Objects.nonNull(reportItem.getInstalledAt()) ?
                                    dateFormatter.format(reportItem.getInstalledAt()) : "-"))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Installed By" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(Objects.nonNull(reportItem.getInstalledBy()) ?
                                    reportItem.getInstalledBy().getName() : "-"))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Measured By" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(Objects.nonNull(reportItem.getCreatedBy()) ?
                                    reportItem.getCreatedBy().getName() : "-"))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Validity" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(Objects.nonNull(reportItem.getExpiry()) ?
                                    String.valueOf(Duration.between(reportItem.getExpiry(),
                                            LocalDate.now().atStartOfDay()).toDays()) : "-"))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Gift" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(String.valueOf(reportItem.getGift())))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Qty" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(String.valueOf(reportItem.getQuantity().getValue())))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                    case "Status" -> table.addCell(new Cell(1, 1)
                            .add(new Paragraph(reportItem.getStatus().toString()))
                            .setFont(rowFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.BLACK)
                            .setTextAlignment(TextAlignment.CENTER));
                }
            }
        }
    }
}
