package com.nailpos.nailposapi.service;

import com.nailpos.nailposapi.dto.InvoiceDetailResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final SpringTemplateEngine templateEngine;

    public byte[] generateInvoicePdf(InvoiceDetailResponseDTO invoice) {
        try {
            // 1. Tạo Context cho Thymeleaf (đổ dữ liệu vào template)
            Context context = new Context();
            context.setVariable("invoice", invoice);

            // 2. Render HTML từ template
            String htmlContent = templateEngine.process("invoice_template", context);

            // 3. Chuyển đổi HTML -> PDF bằng Flying Saucer
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            
            // Quan trọng: Set font chữ để hỗ trợ Tiếng Việt (nếu server có font)
            // renderer.getFontResolver().addFont("path/to/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo file PDF hóa đơn: " + e.getMessage());
        }
    }
}