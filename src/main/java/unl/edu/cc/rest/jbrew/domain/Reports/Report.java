package unl.edu.cc.rest.jbrew.domain.Reports;

import java.util.Date;
import java.util.List;
import unl.edu.cc.rest.jbrew.domain.Kardex.Kardex;

public class Report {

    private int reportId;
    private String reportType;
    private Date generationDate;

    public Report(int reportId, String reportType, Date generationDate) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.generationDate = generationDate;
    }

    public void generateReportStock() {
        // Stock report generation logic
        // The actual display is handled by ReportView.generateReportStock()
    }

    public void generateReportSale() {
        // Sale report generation logic
        // The actual display is handled by ReportView.generateReportSale()
    }

    public void generateReportMovements() {
        // Movements report generation logic
        // The actual display is handled by ReportView.generateReportMovements()
    }

    public void consultKardex(List<Kardex> kardexList) { // Asociación con Kardex
        // The actual display is handled by ReportView.consultKardex()
    }

    public void consultStockAlerts(List<StockAlert> stockAlertList) { // Asociación con StockAlert
        // Alert processing logic - iterate through stock alerts
        // The actual display is handled by ReportView.consultStockAlerts()
    }

    public void exportReport() {
        // Report export logic
        // The actual display is handled by ReportView.exportReport()
    }

    // Getters y Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Date getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }
}
