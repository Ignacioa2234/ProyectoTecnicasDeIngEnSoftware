package edu.mtisw.KartingRM.controllers;

import edu.mtisw.KartingRM.entities.VoucherEntity;
import edu.mtisw.KartingRM.services.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin("*")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendVoucherEmail(@RequestParam Long voucherId, @RequestParam String recipientEmail) {
        VoucherEntity voucher = voucherService.getVoucherById(voucherId)
                .orElse(null);
        if (voucher == null) {
            return new ResponseEntity<>("Voucher not found", HttpStatus.NOT_FOUND);
        }

        voucherService.sendVoucherEmail(voucher, recipientEmail);
        return new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
    }

    @PostMapping("/create-voucher")
    public ResponseEntity<VoucherEntity> createVoucher(@RequestBody VoucherEntity voucher) {
        VoucherEntity created = voucherService.createVoucher(voucher);

        created.getReservation().getParticipants().forEach(p -> {
            voucherService.sendVoucherEmail(created, p.getEmail());
        });

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    @GetMapping("/retrieve-voucher")
    public ResponseEntity<List<VoucherEntity>> getAllVouchers() {
        List<VoucherEntity> vouchers = voucherService.getAllVouchers();
        return new ResponseEntity<>(vouchers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherEntity> getVoucherById(@PathVariable Long id) {
        Optional<VoucherEntity> voucherOpt = voucherService.getVoucherById(id);
        if (voucherOpt.isPresent()) {
            return new ResponseEntity<>(voucherOpt.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/code/{voucherCode}")
    public ResponseEntity<VoucherEntity> getVoucherByCode(@PathVariable String voucherCode) {
        Optional<VoucherEntity> voucherOpt = voucherService.getVoucherByCode(voucherCode);
        if (voucherOpt.isPresent()) {
            return new ResponseEntity<>(voucherOpt.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoucherEntity> updateVoucher(@PathVariable Long id, @RequestBody VoucherEntity voucherDetails) {
        Optional<VoucherEntity> voucherOpt = voucherService.getVoucherById(id);
        if (voucherOpt.isPresent()) {
            VoucherEntity existingVoucher = voucherOpt.get();
            existingVoucher.setVoucherCode(voucherDetails.getVoucherCode());
            existingVoucher.setIssueDate(voucherDetails.getIssueDate());
            existingVoucher.setTax(voucherDetails.getTax());
            existingVoucher.setTotal(voucherDetails.getTotal());
            existingVoucher.setReservation(voucherDetails.getReservation());

            VoucherEntity updatedVoucher = voucherService.updateVoucher(existingVoucher);
            return new ResponseEntity<>(updatedVoucher, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
