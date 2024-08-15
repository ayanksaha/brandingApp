package com.lb.brandingApp.task.service;

import com.itextpdf.text.DocumentException;
import com.lb.brandingApp.category.data.models.response.PageResponseDto;
import com.lb.brandingApp.task.data.models.request.ReportRequestDto;
import com.lb.brandingApp.task.data.models.response.TaskResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@Transactional
public class ReportsService {

    @Autowired
    PdfService pdfService;

    @Autowired
    TaskService taskService;

    public Resource generateReport(
            Long categoryId, Long stateId, Long districtId, ReportRequestDto request,
            LocalDate fromDate, LocalDate toDate, final String sortOrder, final String sortBy) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        log.info("Report generation requested for category {}, state {}, district {} for {} to {}", categoryId, stateId,
                districtId, dateFormatter.format(fromDate), dateFormatter.format(toDate));
        PageResponseDto<TaskResponseDto> tasks = taskService.getAllTasks(
                categoryId, stateId, districtId, 0, Integer.MAX_VALUE, sortBy, sortOrder);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            pdfService.generatePdf(tasks, byteArrayOutputStream, fromDate, toDate, request);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Report generation successful for category {}, state {}, district {} for {} to {}", categoryId, stateId,
                districtId, dateFormatter.format(fromDate), dateFormatter.format(toDate));
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

}
