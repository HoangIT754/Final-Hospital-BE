package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.Jasper.DataRequest;
import com.hospital.backend.service.JasperReportService;
import com.hospital.backend.utils.ExportUtil;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

@Controller
@Tag(name = "Print File", description = "Print File API")
public class JasperController {
    private final JasperReportService jasperReportService;

    public JasperController(JasperReportService jasperReportService) {
        this.jasperReportService = jasperReportService;
    }

    @PostMapping(value = APIConstants.API_GET_CONTENT, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> export(@RequestBody DataRequest request) throws Exception {
        File file = null;
        try {
            // Lấy filename từ service
            String filename = jasperReportService.getFilename(request.getTypeData());

            // Export file
            file = jasperReportService.exportData(new ArrayList<>(), request);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());
            headers.setContentType(MediaType.parseMediaType("application/" + request.getExportType()));
            headers.setContentLength(file.length());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (FileNotFoundException e) {
            throw new Exception("Error export report", e);
        } finally {
            if (file != null && file.exists()) {
                ExportUtil.cleanupFile(file);
            }
        }
    }
}