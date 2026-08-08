package com.example.batch.report;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class UserNameListPdfGenerator {

    public Path generate(String html, Path outputPath) {
        try {
            Files.createDirectories(outputPath.getParent());

            Path fontPath = copyFontToTemporaryFile();

            try(OutputStream outputStream = Files.newOutputStream(outputPath)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFont(fontPath.toFile(), "Noto Sans JP");
                builder.withHtmlContent(html, null);
                builder.toStream(outputStream);
                builder.run();
            }

            return outputPath;

        } catch (Exception e) {
            throw new IllegalStateException("ユーザ名一覧PDFの生成に失敗しました。outputPath=" + outputPath, e);
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
