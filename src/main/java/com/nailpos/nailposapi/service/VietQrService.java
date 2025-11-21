package com.nailpos.nailposapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class VietQrService {

    @Value("${vietqr.bankId}")
    private String bankId;

    @Value("${vietqr.accountNo}")
    private String accountNo;

    @Value("${vietqr.template}")
    private String template;

    /**
     * Tạo nội dung chuyển khoản: "THANH TOAN HD {invoiceId}"
     */
    public String generatePaymentContent(Long invoiceId) {
        return "THANH TOAN HD " + invoiceId;
    }

    /**
     * Tạo URL QuickLink VietQR
     * Format: https://img.vietqr.io/image/<BANK>-<ACC>-<TEMPLATE>.png?amount=<TIEN>&addInfo=<NOI_DUNG>
     */
    public String generateQrUrl(Long invoiceId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        try {
            String content = generatePaymentContent(invoiceId);
            // Encode nội dung để tránh lỗi URL (ví dụ có dấu cách)
            String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8);

            // Xây dựng URL
            return String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%s&addInfo=%s",
                    bankId,
                    accountNo,
                    template,
                    amount.toBigInteger(), // Chuyển về số nguyên (bỏ phần thập phân .00)
                    encodedContent
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}