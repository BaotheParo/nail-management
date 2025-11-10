package com.nailpos.nailposapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO Gốc: Toàn bộ hóa đơn gửi lên từ Frontend
 */
@Data
public class InvoiceRequestDTO {

    // ID khách hàng, có thể null (khách vãng lai)
    private Long customerId;

    @NotNull(message = "Giảm giá không được rỗng (nhập 0 nếu không giảm)")
    @Min(value = 0, message = "Giảm giá không được âm")
    private BigDecimal discountAmount;

    @NotEmpty(message = "Hình thức thanh toán không được rỗng")
    private String paymentMethod; // "Tiền mặt", "Chuyển khoản"

    @Valid // <-- Validate lồng
    @NotEmpty(message = "Hóa đơn phải có ít nhất 1 dịch vụ")
    private List<InvoiceItemRequestDTO> items;
}