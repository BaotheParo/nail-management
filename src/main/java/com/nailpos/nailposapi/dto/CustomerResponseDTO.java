package com.nailpos.nailposapi.dto;

import lombok.Data;

import java.util.Date;
@Data
public class CustomerResponseDTO {
    private Long customerId;
    private String name;
    private String phone;
    private String notes;
    private String status;

    private Double totalSpent;
    private Date lastVisitDate;
}
