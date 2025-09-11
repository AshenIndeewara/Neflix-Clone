package lk.ijse.netflix.contoller;

import lk.ijse.netflix.dto.ApiResponse;
import lk.ijse.netflix.dto.PaymentRequest;
import lk.ijse.netflix.dto.PaymentResponse;
import lk.ijse.netflix.dto.UserData;
import lk.ijse.netflix.entity.Payment;
import lk.ijse.netflix.entity.Role;
import lk.ijse.netflix.repo.PaymentRepository;
import lk.ijse.netflix.service.PayHereService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;

@RestController
@RequestMapping("v1/api/payments")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentController {

    private final PayHereService payHereService;
    private final PaymentRepository paymentRepository;

    @PostMapping
    public PaymentResponse createPayment(@RequestBody PaymentRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData principal = (UserData) auth.getPrincipal();
        System.out.println("Principal : "+principal);
        String orderId = "ORDER_" + System.currentTimeMillis();
        String signature = payHereService.generateSignature(orderId, request.getAmount(), request.getCurrency());
        System.out.println(" Generated signature: " + signature);
        //get year and month
        String yearAndMonth = String.valueOf(new Date().getYear() + 1900) +"-"+ String.format("%02d", new Date().getMonth() + 1);
        Payment payment = Payment.builder()
                .orderId(orderId)
                .plan(request.getPlan())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status("PENDING")
                .userID(principal.getUserId())
                .customerEmail(principal.getEmail())
                .date(yearAndMonth)
                .build();

        payHereService.save(payment);
        System.out.println("💾 Payment saved: " + payment);

        return new PaymentResponse(orderId, signature, principal.getEmail());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,   // page starts at 0
            @RequestParam(defaultValue = "10") int size   // 10 payments per page
    ) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", payHereService.getAllPayments(page, size)));
    }
}
