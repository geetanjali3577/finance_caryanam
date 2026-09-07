package com.finserv.service;

import com.finserv.entity.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    public byte[] createZip(List<Document> documents) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Document doc : documents) {
                if (doc.getFilePath() != null) {
                    Path path = Paths.get(doc.getFilePath());
                    if (Files.exists(path)) {
                        ZipEntry entry = new ZipEntry(doc.getFileName() != null ? doc.getFileName() : path.getFileName().toString());
                        zos.putNextEntry(entry);
                        zos.write(Files.readAllBytes(path));
                        zos.closeEntry();
                    }
                }
            }
        }

        return baos.toByteArray();
    }
}
