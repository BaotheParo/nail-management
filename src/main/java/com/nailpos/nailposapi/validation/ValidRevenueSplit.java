package com.nailpos.nailposapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RevenueSplitValidator.class) // Liên kết đến class logic
@Target({ElementType.TYPE}) // Đặt annotation này ở cấp độ Class
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRevenueSplit {
    String message() default "Tổng số tiền 'bẻ giá' (revenueSplits) phải bằng 'itemPrice'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}