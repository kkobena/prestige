/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import dal.TOfficine;
import dal.TUser;
import java.util.Date;
import javax.ejb.Stateless;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import toolkits.utils.jdom;
import util.DateConverter;

/**
 *
 * @author DICI
 */
@Stateless
public class ReportUtil {

    private static final Logger LOG = Logger.getLogger(ReportUtil.class.getName());

    public JasperReport getReport(String reportName, String reportPath) throws JRException, Exception {
        System.out.println(reportName);
        System.out.println(reportPath);
        try (InputStream resource = new FileInputStream(reportPath + reportName + ".jasper")) { //$NON-NLS-1$
            //$NON-NLS-1$
            return (JasperReport) JRLoader.loadObject(resource);
        } catch (FileNotFoundException e) {
            LOG.log(Level.INFO, "Le fichier n'est pas accessible {0}", reportName);
            return compileReport(reportName, reportPath);
//            return getDefaultReport(reportName, reportPath);

        }

    }

    public JasperReport compileReport(String reportName, String reportPath) throws Exception {
        InputStream in = null;
        InputStream in2 = null;
        FileOutputStream out = null;
        File jasperFile = null;

        try {
//            File jrxmlFile = new File(ReportUtil.class.getResource(reportPath + reportName + ".jrxml").getFile()); 
            File jrxmlFile = new File(reportPath + reportName + ".jrxml");
            File dir = jrxmlFile.getParentFile();
            jasperFile = new File(dir, reportName + ".jasper");
            in = new FileInputStream(jrxmlFile);
//            in = ReportUtil.class.getResourceAsStream(reportPath + reportName + ".jrxml");
            out = new FileOutputStream(jasperFile);
            JasperCompileManager.compileReportToStream(in, out);
            in2 = new FileInputStream(jasperFile);//ReportUtil.class.getResourceAsStream(reportPath + reportName + ".jasper");
            return (JasperReport) JRLoader.loadObject(in2);

        } catch (FileNotFoundException | JRException e) {

            if (jasperFile != null) {
                jasperFile.delete();
            }

            throw e;
        } finally {
            if (in != null) {
                in.close();
            }
            if (in2 != null) {
                in2.close();
            }
            if (out != null) {
                out.close();
            }

        }
    }

    public JasperReport getDefaultReport(String reportName, String reportPath) {
        InputStream resource = null;
        try {
            resource = ReportUtil.class.getResourceAsStream(reportPath + reportName + ".jasper"); //$NON-NLS-1$
            return (JasperReport) JRLoader.loadObject(resource);

        } catch (JRException e) {
            LOG.log(Level.SEVERE, null, e);
            return null;

        } finally {
            try {
                if (resource != null) {
                    resource.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace(System.err);
            }
        }
    }

    public void buildReportEmptyDs(Map<String, Object> parameters, String reportName, String path, String pdfPath) {
        try {
            JasperReport jasperReport = getReport(reportName, path);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);

        } catch (JRException e) {
            e.printStackTrace(System.err);

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void buildReportEmptyDs(Map<String, Object> parameters, String path, String pdfPath) {
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(path);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);

        } catch (JRException e) {
            e.printStackTrace(System.err);

        }
    }

    public Map<String, Object> officineData(TOfficine oTOfficine, TUser op) {
        Map<String, Object> parameters = new HashMap<>();
        String P_H_LOGO = jdom.scr_report_file_logo;
        String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
        String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();
        String P_FOOTER_RC = "";
        parameters.put("P_H_LOGO", P_H_LOGO);
        parameters.put("P_H_INSTITUTION", P_H_INSTITUTION);
        parameters.put("P_PRINTED_BY", " " + op.getStrFIRSTNAME() + "  " + op.getStrLASTNAME());
        parameters.put("P_AUTRE_DESC", oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
        if (oTOfficine.getStrREGISTRECOMMERCE() != null) {
            P_FOOTER_RC += "RC N° " + oTOfficine.getStrREGISTRECOMMERCE();
        }
        if (oTOfficine.getStrCOMPTECONTRIBUABLE() != null) {
            P_FOOTER_RC += " - CC N° " + oTOfficine.getStrCOMPTECONTRIBUABLE();
        }
        if (oTOfficine.getStrREGISTREIMPOSITION() != null) {
            P_FOOTER_RC += " - Régime d'Imposition " + oTOfficine.getStrREGISTREIMPOSITION();
        }
        if (oTOfficine.getStrCENTREIMPOSITION() != null) {
            P_FOOTER_RC += " - Centre des Impôts: " + oTOfficine.getStrCENTREIMPOSITION();
        }

        if (oTOfficine.getStrPHONE() != null) {
            String finalphonestring = oTOfficine.getStrPHONE() != null ? "- Tel: " + DateConverter.phoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
            if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
                String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
                for (String va  : phone) {
                    finalphonestring += " / " + DateConverter.phoneNumberFormat(va);
                }
            }
            P_INSTITUTION_ADRESSE += " -  " + finalphonestring;
        }
        if (oTOfficine.getStrCOMPTEBANCAIRE() != null) {
            P_INSTITUTION_ADRESSE += " - Compte Bancaire: " + oTOfficine.getStrCOMPTEBANCAIRE();
        }
        if (oTOfficine.getStrNUMCOMPTABLE() != null) {
            P_INSTITUTION_ADRESSE += " - CPT N°: " + oTOfficine.getStrNUMCOMPTABLE();
        }
        parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);
        parameters.put("P_FOOTER_RC", P_FOOTER_RC);
        return parameters;
    }

    public void buildReport(Map<String, Object> parameters, String reportName, String path, String pdfPath, List<?> datas) {
        try {
            JasperReport jasperReport = getReport(reportName, path);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datas);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);

        } catch (JRException e) {
            e.printStackTrace(System.err);

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public Map<String, Object> ticketParamsCommons(TOfficine oTOfficine) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("raisonsocial", oTOfficine.getStrNOMCOMPLET());
        parameters.put("sectionInfos", oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
        parameters.put("firstComment", oTOfficine.getStrENTETE());
        parameters.put("thanksMsg", oTOfficine.getStrCOMMENTAIRE1());
        parameters.put("adressPhone", oTOfficine.getStrPHONE() + "   |    " + oTOfficine.getStrADRESSSEPOSTALE());
        return parameters;

    }

    public Map<String, Object> ticketParams(Map<String, Object> parameters, String modeReglement, int net) {
        parameters.put("totalvente", net);
        parameters.put("modeReglement", modeReglement);
        return parameters;

    }

    public Map<String, Object> numTicketParams(Map<String, Object> parameters, String ticketNum) {
        parameters.put("ticketNum", "Ticket # " + ticketNum);
        return parameters;

    }

    public Map<String, Object> operateurParams(Map<String, Object> parameters, String fullName) {
        parameters.put("operateur", fullName);
        return parameters;

    }

    public Map<String, Object> setSignature(Map<String, Object> parameters, String signature) {
        parameters.put("signature", signature);
        return parameters;

    }

    public Map<String, Object> barecodeDataParams(Map<String, Object> parameters, String barcodeData) {
        parameters.put("barcodeData", barcodeData);
        return parameters;

    }

    public Map<String, Object> ticketParamsMontantVerse(Map<String, Object> parameters, int montantVerse, int montantRendu) {
        parameters.put("montantVerse", montantVerse);
        parameters.put("montantRendu", montantRendu);
        return parameters;

    }

    public Map<String, Object> ticketParams(Map<String, Object> parameters, String ticketNum, Date dateOperation, String infosCaisse) {
        parameters.put("dateoperation", dateOperation);
        parameters.put("infosCaisse", infosCaisse);
        return parameters;

    }

    public Map<String, Object> carnetTpParams(Map<String, Object> parameters, String clientFullName, String matricule, int montantClient, String tierpayantName, int tauxtp, int partTp) {
        parameters.put("matricule ", matricule);
        parameters.put("clientFullName", clientFullName);
        parameters.put("montantClient", montantClient);
        parameters.put("tierpayantName", tierpayantName);
        parameters.put("tauxtp", tauxtp);
        parameters.put("partTp", partTp);
        return parameters;

    }

    public void printTicket(Map<String, Object> parameters, String reportName, String path, PrintService printService, List<?> datas) {
        try {
//            PrinterJob job = PrinterJob.getPrinterJob();
//            job.setPrintService(printService);

            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(MediaSizeName.ISO_A4);

//            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
//             printServiceAttributeSet.add(new PrinterName("Foxit Reader PDF Printer Driver", Locale.getDefault()));
            JasperReport jasperReport = getReport(reportName, path);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datas);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();

            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
            configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
            configuration.setPrintService(printService);
//             configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
            configuration.setDisplayPageDialog(false);
            configuration.setDisplayPrintDialog(false);
            exporter.setConfiguration(configuration);
            exporter.exportReport();

        } catch (JRException e) {
            e.printStackTrace(System.err);

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
