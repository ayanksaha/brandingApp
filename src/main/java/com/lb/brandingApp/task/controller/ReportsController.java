package com.lb.brandingApp.task.controller;

import com.lb.brandingApp.task.data.models.request.ReportRequestDto;
import com.lb.brandingApp.task.service.ReportsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @PostMapping(value = "/app/category/{category_id}/state/{state_id}/district/{district_id}/reports",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> generateReport(
            @RequestBody ReportRequestDto request, @PathVariable("category_id") Long categoryId,
            @PathVariable("state_id") Long stateId, @PathVariable("district_id") Long districtId,
            @RequestParam(value = "from_date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
            @RequestParam(value = "to_date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDate) {
        return ResponseEntity.ok(reportsService.generateReport(categoryId, stateId, districtId,
                request, fromDate, toDate,null, null));
    }
}
