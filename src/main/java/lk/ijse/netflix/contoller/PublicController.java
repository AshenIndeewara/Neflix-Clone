package lk.ijse.netflix.contoller;

import lk.ijse.netflix.dto.ApiResponse;
import lk.ijse.netflix.service.PayHereService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("public")
@RequiredArgsConstructor
@CrossOrigin
public class PublicController {
    private final PayHereService payHereService;
    @Value("${spring.datasource.url}")
    private String sqlUrl;

    @PostMapping("notify")
    public ResponseEntity<ApiResponse> notifyPayment(@RequestBody Map<String, Object> request) throws Exception {
        System.out.println("Payment notification received: " + request);
        String captured_amount = (String) request.get("captured_amount");
        String order_id = (String) request.get("order_id");
        String status_code = (String) request.get("status_code");
        String md5sig = (String) request.get("md5sig");

        return ResponseEntity.ok(new ApiResponse(200, "OK", payHereService.handlePaymentNotification(captured_amount, order_id, status_code, md5sig)));
    }

    @GetMapping("env")
    public ResponseEntity<ApiResponse> getEnv() throws IOException {
        return ResponseEntity.ok(new ApiResponse(200, "OK", sqlUrl));
    }
}
