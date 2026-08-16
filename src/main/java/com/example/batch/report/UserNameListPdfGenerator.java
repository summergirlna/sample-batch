package com.example.batch.report;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class UserNameListPdfGenerator {

    public byte[] generate(String html) {
        try {
            Path fontPath = copyFontToTemporaryFile();

            try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFont(fontPath.toFile(), "Noto Sans JP");
                builder.withHtmlContent(html, null);
                builder.toStream(outputStream);
                builder.run();

                return outputStream.toByteArray();
            }

        } catch (Exception e) {
            throw new IllegalStateException("ユーザ名一覧PDFの生成に失敗しました。" , e);
        }
    }

    private Path copyFontToTemporaryFile() throws Exception {
        ClassPathResource fontResource = new ClassPathResource("fonts/NotoSansJP-Regular.ttf");

        Path fontPath = Files.createTempFile("NotoSansJP-Regular", ".ttf");
        try(InputStream inputStream = fontResource.getInputStream()) {
            Files.copy(inputStream, fontPath, REPLACE_EXISTING);
        }

        return fontPath;
    }
}
