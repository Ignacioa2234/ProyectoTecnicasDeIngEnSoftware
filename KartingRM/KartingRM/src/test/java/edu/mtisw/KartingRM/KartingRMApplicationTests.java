package edu.mtisw.KartingRM;

import edu.mtisw.KartingRM.entities.ClientEntity;
import edu.mtisw.KartingRM.entities.KartEntity;
import edu.mtisw.KartingRM.entities.ReportEntity;
import edu.mtisw.KartingRM.entities.ReservationEntity;
import edu.mtisw.KartingRM.entities.VoucherEntity;
import edu.mtisw.KartingRM.repositories.ClientRepository;
import edu.mtisw.KartingRM.repositories.KartRepository;
import edu.mtisw.KartingRM.repositories.ReportRepository;
import edu.mtisw.KartingRM.repositories.ReservationRepository;
import edu.mtisw.KartingRM.repositories.VoucherRepository;
import edu.mtisw.KartingRM.services.ClientService;
import edu.mtisw.KartingRM.services.KartService;
import edu.mtisw.KartingRM.services.ReportService;
import edu.mtisw.KartingRM.services.ReservationService;
import edu.mtisw.KartingRM.services.VoucherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KartingRMApplicationTests {

	@Mock
	private ClientRepository clientRepository;
	@Mock
	private ReservationRepository reservationRepository;
	@Mock
	private KartRepository kartRepository;
	@Mock
	private ReportRepository reportRepository;
	@Mock
	private VoucherRepository voucherRepository;
	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private ClientService clientService;
	@InjectMocks
	private ReservationService reservationService;
	@InjectMocks
	private KartService kartService;
	@InjectMocks
	private ReportService reportService;
	@InjectMocks
	private VoucherService voucherService;

	@Test
	void calculateDiscountForClient_whenNoClient_returnsZero() {
		when(clientRepository.findById(42L)).thenReturn(Optional.empty());
		double discount = clientService.calculateDiscountForClient(42L);
		assertEquals(0.0, discount);
		verify(clientRepository).findById(42L);
	}

	@Test
	void getAllReservations_delegatesToRepository() {
		ReservationEntity e = new ReservationEntity();
		when(reservationRepository.findAll()).thenReturn(List.of(e));
		var list = reservationService.getAllReservations();
		assertEquals(1, list.size());
		assertSame(e, list.get(0));
		verify(reservationRepository).findAll();
	}

	@Test
	void getAllKarts_delegatesToRepository() {
		KartEntity k = new KartEntity();
		when(kartRepository.findAll()).thenReturn(List.of(k));
		var list = kartService.getAllKarts();
		assertEquals(1, list.size());
		assertSame(k, list.get(0));
		verify(kartRepository).findAll();
	}

	@Test
	void getAllReports_returnsRepoList() {
		ReportEntity r = new ReportEntity();
		when(reportRepository.findAll()).thenReturn(List.of(r));
		var list = reportService.getAllReports();
		assertEquals(1, list.size());
		assertSame(r, list.get(0));
		verify(reportRepository).findAll();
	}

	@Test
	void getAllVouchers_delegatesToRepository() {
		VoucherEntity v = new VoucherEntity();
		when(voucherRepository.findAll()).thenReturn(List.of(v));
		var list = voucherService.getAllVouchers();
		assertEquals(1, list.size());
		assertSame(v, list.get(0));
		verify(voucherRepository).findAll();
	}
}
