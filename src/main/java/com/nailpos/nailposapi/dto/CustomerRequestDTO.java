package com.nailpos.nailposapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequestDTO {
    @NotEmpty(message = "Tên khách hàng không được để trống")
    @Size(max = 100, message = "Tên không quá 100 ký tự")
    private String name;

    @Size(max = 20, message = "SĐT không quá 20 ký tự")
    private String phone;

    private String notes;
}
