package com.finserv.service;

import com.finserv.entity.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    public byte[] createZip(List<Document> documents)
            throws IOException {

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        try (ZipOutputStream zos =
                     new ZipOutputStream(baos)) {

            for (Document doc : documents) {

                ZipEntry entry =
                        new ZipEntry(doc.getFileName());

                zos.putNextEntry(entry);

                zos.write(doc.getFileData());

                zos.closeEntry();
            }
        }

        return baos.toByteArray();
    }
}
