package edu.mtisw.KartingRM.services;

import edu.mtisw.KartingRM.entities.ReportEntity;
import edu.mtisw.KartingRM.entities.VoucherEntity;
import edu.mtisw.KartingRM.repositories.ReportRepository;
import edu.mtisw.KartingRM.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    public List<ReportEntity> getLapsTimeReport(LocalDateTime start, LocalDateTime end) {
        return reportRepository.reportByLapsOrTime(start, end);
    }

    public List<ReportEntity> getPeopleCountReport(LocalDateTime start, LocalDateTime end) {
        return reportRepository.reportByGroupSize(start, end);
    }

    public List<ReportEntity> getAllReports() {
        return reportRepository.findAll();
    }

    public List<ReportEntity> getReportsByType(String type) {
        return reportRepository.findAllByReportType(type);
    }

    public ReportEntity saveReport(ReportEntity report) {
        report.setReportDate(LocalDateTime.now());
        return reportRepository.save(report);
    }

    public List<VoucherEntity> getWeeklySchedule(LocalDateTime start, LocalDateTime end) {
        return voucherRepository.findAllByReservationReservationDateTimeBetween(start, end);
    }
}
