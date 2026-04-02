package com.hospital.backend.service;

import com.hospital.backend.dto.request.Jasper.DataRequest;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.repository.PatientProfileRepository;
import com.hospital.backend.utils.ExportUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class JasperReportService {

    private final PatientProfileRepository patientProfileRepository;

    public JasperReportService(PatientProfileRepository patientProfileRepository) {
        this.patientProfileRepository = patientProfileRepository;
    }

    //
    public File exportData(List<Object> listData, DataRequest request) {
        if (!listData.isEmpty()) {
            listData.addFirst(new Object());
        }

        File file = null;
        try {
            Path baseDir = ExportUtil.getSwiftTempDirectory();
            Files.createDirectories(baseDir);
            Path secureTempDir = Files.createTempDirectory(baseDir, "secureDir_");
            file = Files.createTempFile(secureTempDir, "out_", ".tmp").toFile();
            file.deleteOnExit();

            String typeData = request.getTypeData();
            String templatePath = String.format("templates/jasper/%s.jasper", typeData);

            Resource resource = new ClassPathResource(templatePath);
            try (InputStream inputStream = resource.getInputStream();
                 FileOutputStream fos = new FileOutputStream(file)) {

                TemplateConfig config = getTemplateConfig(typeData);

                Map<String, Object> parameters = getDefaultParameters(request);

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa");
                String strDate = formatter.format(new Date());
                parameters.put("printDate", strDate);
                parameters.put("qfFooter", "docx".equals(request.getExportType()) || "word".equals(request.getExportType()) ? config.footer : null);

                ExportUtil.exportReport(inputStream, fos, parameters, listData, request.getExportType(),
                        config.footer, config.hasPageNumber, typeData);

            } catch (Exception e) {
                log.error("Error while exporting report: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Error while creating temp file: {}", e.getMessage());
        }
        return file;
    }

    // Set cứng thông tin template
    private TemplateConfig getTemplateConfig(String typeData) {
        String fileName = "Patient_Report"+ getTimeStamp() +".pdf";
        switch (typeData) {
            case "QF_A.01":
                return new TemplateConfig(fileName, "Footer for Patient Report", true);
            case "QF_A.02":
                return new TemplateConfig("Medical_Report.pdf", "Footer for Medical Report", true);
            case "QF_A.03":
                return new TemplateConfig("Billing_Report.pdf", null, false);
            default:
                return new TemplateConfig("General_Report.pdf", null, false);
        }
    }

    private String getTimeStamp(){
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(currentTime);
    }

    private Map<String, Object> getDefaultParameters(DataRequest request) {
        Map<String, Object> params = new HashMap<>();

        params.put("title", "Hospital Report");
        params.put("hospitalName", "ACB Hospital");

        List<PatientProfile> patientProfiles = patientProfileRepository.findAll();

        switch (request.getTypeData()) {
            case "QF_A.01":
                JRBeanCollectionDataSource ds = getTableData(patientProfiles);
                params.put("datasource1", ds);
                break;
            case "QF_A.02":
                params.put("reportType", "Medical Report");
                params.put("department", "Laboratory");
                break;
            case "QF_A.03":
                params.put("reportType", "Billing Report");
                params.put("department", "Finance");
                break;
            default:
                params.put("reportType", "General Report");
                params.put("department", "Administration");
        }

        return params;
    }

    private static class TemplateConfig {
        String filename;
        String footer;
        boolean hasPageNumber;

        TemplateConfig(String filename, String footer, boolean hasPageNumber) {
            this.filename = filename;
            this.footer = footer;
            this.hasPageNumber = hasPageNumber;
        }
    }

    public String getFilename(String typeData) {
        return getTemplateConfig(typeData).filename;

    }

    private static JRBeanCollectionDataSource getTableData(List<PatientProfile> patientProfiles) {
        List<Map<String, Object>> dataset1Data = new ArrayList<>();

        for (PatientProfile patient : patientProfiles) {
            Map<String, Object> row = new HashMap<>();
            row.put("1", safe(patient.getFirstName()));
            row.put("2", safe(patient.getLastName()));
            row.put("3", safe(patient.getGender()));
            row.put("4", safe(patient.getMedicalHistory()));
            row.put("5", safe(patient.getPhoneNumber()));
            row.put("6", safe(patient.getDateOfBirth()));
            row.put("7", safe(patient.getHealthInsuranceNumber()));

            dataset1Data.add(row);
        }

        return new JRBeanCollectionDataSource(dataset1Data);
    }
    private static String safe(Object value) {
        return value != null ? value.toString() : "";
    }

}