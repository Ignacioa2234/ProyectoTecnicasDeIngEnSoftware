// src/test/java/edu/mtisw/KartingRM/ReportServiceTest.java
package edu.mtisw.KartingRM;

import edu.mtisw.KartingRM.entities.ReportEntity;
import edu.mtisw.KartingRM.entities.VoucherEntity;
import edu.mtisw.KartingRM.repositories.ReportRepository;
import edu.mtisw.KartingRM.repositories.VoucherRepository;
import edu.mtisw.KartingRM.services.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock ReportRepository reportRepo;
    @Mock VoucherRepository voucherRepo;
    @InjectMocks ReportService reportService;

    @Test
    void getLapsAndPeopleReports_delegates() {
        var now = LocalDateTime.now();
        List<ReportEntity> dummy = List.of(new ReportEntity());
        when(reportRepo.reportByLapsOrTime(now, now)).thenReturn(dummy);
        assertSame(dummy, reportService.getLapsTimeReport(now, now));
        when(reportRepo.reportByGroupSize(now, now)).thenReturn(dummy);
        assertSame(dummy, reportService.getPeopleCountReport(now, now));
    }

    @Test
    void getAllAndByType_delegates() {
        List<ReportEntity> all = List.of(new ReportEntity());
        when(reportRepo.findAll()).thenReturn(all);
        assertSame(all, reportService.getAllReports());
        when(reportRepo.findAllByReportType("T")).thenReturn(all);
        assertSame(all, reportService.getReportsByType("T"));
    }

    @Test
    void saveReport_setsDateAndSaves() {
        ReportEntity r = new ReportEntity();
        when(reportRepo.save(r)).thenReturn(r);
        var out = reportService.saveReport(r);
        assertNotNull(out.getReportDate());
        verify(reportRepo).save(r);
    }

    @Test
    void getWeeklySchedule_delegates() {
        var now = LocalDateTime.now();
        List<VoucherEntity> v = List.of(new VoucherEntity());
        when(voucherRepo.findAllByReservationReservationDateTimeBetween(now, now))
            .thenReturn(v);
        assertSame(v, reportService.getWeeklySchedule(now, now));
    }
}
