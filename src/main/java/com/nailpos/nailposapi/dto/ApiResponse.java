package com.nailpos.nailposapi.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
@Setter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object errors;
    private PageMetadata pagination;

    // Constructor cho dữ liệu đơn (T)
    private ApiResponse(T data, String message) {
        this.success = true;
        this.message = message;
        this.data = data;
    }

    // Constructor cho dữ liệu Page
    private ApiResponse(Page<?> pageData, String message, T dataList) {
        this.success = true;
        this.message = message;
        this.data = dataList;
        this.pagination = new PageMetadata(pageData);
    }

    // Constructor cho lỗi
    private ApiResponse(String message, Object errors) {
        this.success = false;
        this.message = message;
        this.errors = errors;
    }

    // === THÊM MỚI CONSTRUCTOR NÀY ===
    // Constructor cho thành công, không có data
    private ApiResponse(String message) {
        this.success = true;
        this.message = message;
        this.data = null; // data là null
    }

    // --- Các hàm static tiện ích ---

    // Hàm ok cho dữ liệu đơn
    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    // Hàm ok cho Page
    public static <T> ApiResponse<List<T>> ok(Page<T> pageData, String message) {
        return new ApiResponse<>(pageData, message, pageData.getContent());
    }

    // === THÊM MỚI PHƯƠNG THỨC NÀY ===
    // Hàm ok cho thành công, không có data (ví dụ: delete)
    // Trả về ApiResponse<Object> để khớp với controller
    public static ApiResponse<Object> ok(String message) {
        return new ApiResponse<>(message);
    }

    // Hàm created (giống ok)
    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    // Hàm error (không đổi)
    public static ApiResponse<Object> error(String message, Object errors) {
        return new ApiResponse<>(message, errors);
    }

    // Hàm notFound (không đổi)
    public static ApiResponse<Object> notFound(String message) {
        return error(message, "Resource not found");
    }

    // Class nội bộ PageMetadata (Không đổi)
    @Getter
    @Setter
    static class PageMetadata {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;

        PageMetadata(Page<?> page) {
            this.page = page.getNumber();
            this.size = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.last = page.isLast();
        }
    }
}