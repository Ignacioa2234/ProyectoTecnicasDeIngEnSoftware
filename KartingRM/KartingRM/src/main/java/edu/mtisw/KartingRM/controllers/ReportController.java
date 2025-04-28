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
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/laps-time")
    public ResponseEntity<List<ReportEntity>> getLapsTimeReport(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime   = end.atTime(LocalTime.MAX);
        List<ReportEntity> reports  = reportService.getLapsTimeReport(startDateTime, endDateTime);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @GetMapping("/group-size")
    public ResponseEntity<List<ReportEntity>> getGroupSizeReport(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime   = end.atTime(LocalTime.MAX);
        List<ReportEntity> reports  = reportService.getPeopleCountReport(startDateTime, endDateTime);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

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

    @PostMapping("/generate")
    public ResponseEntity<List<ReportEntity>> generateReport(@RequestBody ReportRequest request) {
        LocalDateTime start = request.getStart();
        LocalDateTime end   = request.getEnd();
        String type         = request.getReportType();

        List<ReportEntity> reports;
        if ("laps".equalsIgnoreCase(type)) {
            reports = reportService.getLapsTimeReport(start, end);
        } else if ("group-size".equalsIgnoreCase(type)) {
            reports = reportService.getPeopleCountReport(start, end);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<VoucherEntity>> getWeeklySchedule(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime   = end.atTime(LocalTime.MAX);
        List<VoucherEntity> schedule = reportService.getWeeklySchedule(startDateTime, endDateTime);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    public static class ReportRequest {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime start;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime end;
        private String reportType;
        public LocalDateTime getStart() { return start; }
        public void setStart(LocalDateTime start) { this.start = start; }
        public LocalDateTime getEnd() { return end; }
        public void setEnd(LocalDateTime end) { this.end = end; }
        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
    }
}
