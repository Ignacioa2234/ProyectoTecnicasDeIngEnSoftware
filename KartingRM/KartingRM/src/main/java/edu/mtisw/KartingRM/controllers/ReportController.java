package edu.mtisw.KartingRM.controllers;

import edu.mtisw.KartingRM.entities.ReportEntity;
import edu.mtisw.KartingRM.entities.VoucherEntity;
import edu.mtisw.KartingRM.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<List<ReportEntity>> getAllReports(
            @RequestParam(value = "type", required = false) String type
    ) {
        List<ReportEntity> reports;
        if (type != null && !type.isEmpty()) {
            reports = reportService.getReportsByType(type);
        } else {
            reports = reportService.getAllReports();
        }
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @GetMapping("/laps-time")
    public ResponseEntity<List<ReportEntity>> getLapsTimeReport(
            @RequestParam("start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate start,

            @RequestParam("end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate end
    ) {
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to   = end.atTime(23,59,59);
        List<ReportEntity> reports = reportService.getLapsTimeReport(from, to);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/group-size")
    public ResponseEntity<List<ReportEntity>> getGroupSizeReport(
            @RequestParam("start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate start,

            @RequestParam("end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate end
    ) {
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to   = end.atTime(23,59,59);
        List<ReportEntity> reports = reportService.getPeopleCountReport(from, to);
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/generate")
    public ResponseEntity<List<ReportEntity>> generateReport(
            @RequestBody ReportRequest request
    ) {
        LocalDateTime from = request.getStart().atStartOfDay();
        LocalDateTime to   = request.getEnd().atTime(23,59,59);
        String type        = request.getReportType();

        List<ReportEntity> computed;
        if ("laps".equalsIgnoreCase(type)) {
            computed = reportService.getLapsTimeReport(from, to);
        } else if ("group-size".equalsIgnoreCase(type)) {
            computed = reportService.getPeopleCountReport(from, to);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Persistimos cada entidad y devolvemos la lista de guardados
        List<ReportEntity> saved = new ArrayList<>();
        for (ReportEntity r : computed) {
            saved.add(reportService.saveReport(r));
        }
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<VoucherEntity>> getWeeklySchedule(
            @RequestParam("start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam("end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end
    ) {
        List<VoucherEntity> schedule = reportService.getWeeklySchedule(start, end);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    public static class ReportRequest {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate start;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate end;
        private String reportType;

        public LocalDate getStart() { return start; }
        public void setStart(LocalDate start) { this.start = start; }
        public LocalDate getEnd() { return end; }
        public void setEnd(LocalDate end) { this.end = end; }
        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
    }
}
