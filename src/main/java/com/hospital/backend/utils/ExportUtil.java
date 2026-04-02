package com.hospital.backend.utils;


import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.SimplePdfExporterConfiguration;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ExportUtil {

    public static void exportReportDynamic(JasperReport jasperReport, OutputStream outputStream, Map<String, Object> parameters,
                                           List<?> exportList, String type, String qfFooter, boolean hasPageNumber, String typeQf ) throws JRException, URISyntaxException {
        JRDataSource dataSource = (JRDataSource) createJRDataSource(exportList);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        export(jasperPrint, type, outputStream, qfFooter, hasPageNumber,typeQf);
    }

    public static void exportReport(InputStream inputStream, OutputStream outputStream, Map<String, Object> parameters,
                                    List<?> exportList, String type, String qfFooter, boolean hasPageNumber, String typeQf) throws JRException, URISyntaxException {
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
        JRDataSource dataSource = (JRDataSource) createJRDataSource(exportList);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        export(jasperPrint, type, outputStream, qfFooter, hasPageNumber, typeQf);
    }

    public static Object createJRDataSource( List<?> data) {
        return data != null && !data.isEmpty() ? new JRBeanCollectionDataSource(data) : new JREmptyDataSource();
    }

    private static void export(JasperPrint jasperPrint, String type, OutputStream outputStream, String qfFooter, boolean hasPageNumber, String typeQf) throws JRException, URISyntaxException {
        switch (type.toLowerCase()) {
            case "pdf":
                exportPDF(jasperPrint, outputStream);
                break;
            case "docx":
            case "word":
                exportDOCX(jasperPrint, outputStream, qfFooter, hasPageNumber, typeQf);
                break;
            case "xlsx":
                exportXLSX(jasperPrint, outputStream);
                break;
            default:
                throw new JRException("System not support export reports type " + type);
        }
    }

    private static void exportPDF(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.setConfiguration(new SimplePdfExporterConfiguration());
        exporter.exportReport();
    }


    private static void exportDOCX(JasperPrint jasperPrint, OutputStream outputStream, String qfFooter, boolean hasPageNumber, String typeQf) throws JRException {
        ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();

        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tempOutput));

        SimpleDocxReportConfiguration configuration = new SimpleDocxReportConfiguration();
        configuration.setFlexibleRowHeight(!typeQf.equalsIgnoreCase("QF_A.01"));
        configuration.setFramesAsNestedTables(false);
        configuration.setIgnoreHyperlink(true);
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        if ((qfFooter != null && !qfFooter.isEmpty()) || hasPageNumber) {
            try {
                addFooterWithMarginsAndPageNumber(
                        new ByteArrayInputStream(tempOutput.toByteArray()),
                        outputStream,
                        qfFooter,
                        hasPageNumber,
                        0, 200, 0, 0
                );
            } catch (Exception e) {
                throw new JRException("Error adding footer with margins to DOCX", e);
            }
        } else {
            try {
                addFooterWithMarginsAndPageNumber(new ByteArrayInputStream(tempOutput.toByteArray()), outputStream);
            } catch (Exception e) {
                throw new JRException("Error adding footer with margins to DOCX", e);
            }
        }
    }

    // Utility method to create footer with specific margins
    private static void addFooterWithMarginsAndPageNumber(
            InputStream docxInput,
            OutputStream outputStream,
            String footerText,
            boolean hasPageNumber,
            int topMargin,
            int bottomMargin,
            int leftMargin,
            int rightMargin) throws Exception {

        try (XWPFDocument document = new XWPFDocument(docxInput)) {
            XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);

            addTopSpacing(footer, topMargin);

            XWPFParagraph footerParagraph = createFooterParagraph(footer, leftMargin, rightMargin);
            addFooterText(footerParagraph, footerText);
            if (hasPageNumber) {
                addPageNumber(footerParagraph, footerText);
            }

            addBottomSpacing(footer, bottomMargin);
            setPageMargins(document);

            document.write(outputStream);
        }
    }

    private static void addTopSpacing(XWPFFooter footer, int topMargin) {
        if (topMargin > 0) {
            XWPFParagraph topSpacing = footer.createParagraph();
            topSpacing.setSpacingBefore(topMargin);
        }
    }

    private static void addBottomSpacing(XWPFFooter footer, int bottomMargin) {
        if (bottomMargin > 0) {
            XWPFParagraph bottomSpacing = footer.createParagraph();
            bottomSpacing.setSpacingAfter(bottomMargin);
        }
    }

    private static XWPFParagraph createFooterParagraph(XWPFFooter footer, int leftMargin, int rightMargin) {
        XWPFParagraph paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        if (leftMargin > 0) paragraph.setIndentationLeft(leftMargin);
        if (rightMargin > 0) paragraph.setIndentationRight(rightMargin);
        return paragraph;
    }

    private static void addFooterText(XWPFParagraph paragraph, String footerText) {
        if (footerText != null && !footerText.isEmpty()) {
            XWPFRun leftRun = paragraph.createRun();
            leftRun.setText(footerText);
            leftRun.setFontSize(10);
        }
    }

    private static void addPageNumber(XWPFParagraph paragraph, String footerText) {
        addRightAlignedTabStop(paragraph);

        if (footerText != null && !footerText.isEmpty()) {
            XWPFRun tabRun = paragraph.createRun();
            tabRun.addTab();
        }

        XWPFRun pageRun = paragraph.createRun();
        CTR ctr = pageRun.getCTR();
        ctr.addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        ctr.addNewInstrText().setStringValue("PAGE");
        ctr.addNewFldChar().setFldCharType(STFldCharType.END);
        pageRun.setFontSize(10);
    }

    private static void addRightAlignedTabStop(XWPFParagraph paragraph) {
        CTTabStop tabStop = CTTabStop.Factory.newInstance();
        tabStop.setPos(BigInteger.valueOf(8500));
        tabStop.setVal(STTabJc.RIGHT);

        if (paragraph.getCTP().getPPr() == null) {
            paragraph.getCTP().addNewPPr();
        }
        if (paragraph.getCTP().getPPr().getTabs() == null) {
            paragraph.getCTP().getPPr().addNewTabs();
        }
        paragraph.getCTP().getPPr().getTabs().addNewTab().set(tabStop);
    }

    private static void setPageMargins(XWPFDocument document) {
        CTSectPr sectPr = document.getDocument().getBody().getSectPr();
        if (sectPr == null) {
            sectPr = document.getDocument().getBody().addNewSectPr();
        }

        CTPageMar pageMar = sectPr.getPgMar();
        if (pageMar == null) {
            pageMar = sectPr.addNewPgMar();
        }

        pageMar.setTop(BigInteger.valueOf(1134));   // 2cm
        pageMar.setBottom(BigInteger.valueOf(1134)); // 2cm
        pageMar.setLeft(BigInteger.valueOf(1440));  // 2.54cm
        pageMar.setRight(BigInteger.valueOf(1134)); // 2cm
    }


    private static void addFooterWithMarginsAndPageNumber(InputStream docxInput, OutputStream outputStream) throws Exception {
        XWPFDocument document = new XWPFDocument(docxInput);

        CTSectPr sectPr = document.getDocument().getBody().getSectPr();
        if (sectPr == null) {
            sectPr = document.getDocument().getBody().addNewSectPr();
        }
        CTPageMar pageMar = sectPr.getPgMar();
        if (pageMar == null) {
            pageMar = sectPr.addNewPgMar();
        }

        pageMar.setTop(BigInteger.valueOf(1134));  // 2cm
        pageMar.setBottom(BigInteger.valueOf(1134)); // 2cm
        pageMar.setLeft(BigInteger.valueOf(1440));   // 2.54cm
        pageMar.setRight(BigInteger.valueOf(1134));  // 2cm

        document.write(outputStream);
        document.close();
        docxInput.close();
    }

    private static void exportXLSX(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(Boolean.FALSE);
        configuration.setDetectCellType(Boolean.TRUE);
        configuration.setCollapseRowSpan(Boolean.FALSE);
        configuration.setWhitePageBackground(Boolean.FALSE);
        configuration.setDetectCellType(Boolean.TRUE);
        configuration.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);
        configuration.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
        exporter.setConfiguration(configuration);
        exporter.exportReport();
    }

    public static Path getSwiftTempDirectory() {
        String tempDir = System.getenv("APP_TEMP_DIR");
        if (tempDir != null && !tempDir.isEmpty()) {
            return Paths.get(tempDir);
        }

        // Fallback to system temp
        return Paths.get(System.getProperty("user.home"), "appTemp");
    }

    public static void cleanupFile(File file) {
        try {
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.debug("Successfully deleted temp file: {}", file.getAbsolutePath());
                } else {
                    log.warn("Failed to delete temp file: {}", file.getAbsolutePath());
                }

                // Also try to clean up the parent directory if it's empty
                File parentDir = file.getParentFile();
                if (parentDir != null && parentDir.exists() && Objects.requireNonNull(parentDir.list()).length == 0) {
                    boolean isDeleted = parentDir.delete();
                    if(isDeleted) {
                        log.debug("Cleaned up empty temp directory: {}", parentDir.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error cleaning up temp file {}: {}", file.getAbsolutePath(), e.getMessage());
        }
    }
}

