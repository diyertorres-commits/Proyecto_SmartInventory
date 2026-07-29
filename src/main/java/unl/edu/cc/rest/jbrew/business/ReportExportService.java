package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import unl.edu.cc.rest.jbrew.business.ReporteService.DatoReporte;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Stateless
public class ReportExportService {

    public void exportPDF(List<ReporteService.DatoReporte> datosReporte, String tipoReporte, 
                         double totalVentas, double ganancia, int totalTransacciones, double margenPromedio,
                         HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_" + tipoReporte + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf");
        
        ServletOutputStream out = response.getOutputStream();
        com.itextpdf.kernel.pdf.PdfWriter pdfWriter = new com.itextpdf.kernel.pdf.PdfWriter(out);
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(pdfWriter);
        Document document = new Document(pdfDoc);
        
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        Paragraph title = new Paragraph("Reporte: " + getTituloReporte(tipoReporte))
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);
        
        Paragraph date = new Paragraph("Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()))
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20);
        document.add(date);
        
        Paragraph kpiTitle = new Paragraph("Resumen")
                .setFont(boldFont)
                .setFontSize(12)
                .setMarginBottom(10);
        document.add(kpiTitle);
        
        document.add(new Paragraph("Total Ventas: $" + String.format("%.2f", totalVentas)).setFont(normalFont).setFontSize(10));
        document.add(new Paragraph("Ganancia Neta: $" + String.format("%.2f", ganancia)).setFont(normalFont).setFontSize(10));
        document.add(new Paragraph("Transacciones: " + totalTransacciones).setFont(normalFont).setFontSize(10));
        document.add(new Paragraph("Margen Promedio: " + String.format("%.2f", margenPromedio) + "%").setFont(normalFont).setFontSize(10));
        
        document.add(new Paragraph("\n"));
        Paragraph tableTitle = new Paragraph("Detalle del Reporte")
                .setFont(boldFont)
                .setFontSize(12)
                .setMarginBottom(10);
        document.add(tableTitle);
        
        Table table = new Table(8).useAllAvailableWidth();
        
        String[] headers = {"Fecha", "Descripción", "Cantidad", "Precio Unit.", "Total", "Cliente/Prov.", "Método", "Ganancia"};
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setFont(boldFont).setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE))
                    .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (ReporteService.DatoReporte dato : datosReporte) {
            table.addCell(new Cell().add(new Paragraph(dato.getFecha() != null ? sdf.format(dato.getFecha()) : "").setFont(normalFont).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(dato.getDescripcion()).setFont(normalFont).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(dato.getCantidad())).setFont(normalFont).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", dato.getPrecioUnitario())).setFont(normalFont).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", dato.getTotal())).setFont(normalFont).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(dato.getTercero()).setFont(normalFont).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(dato.getMetodo()).setFont(normalFont).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", dato.getGanancia())).setFont(normalFont).setFontSize(9)));
        }
        
        document.add(table);
        document.close();
        out.close();
    }

    public void exportExcel(List<ReporteService.DatoReporte> datosReporte, String tipoReporte,
                           double totalVentas, double ganancia, int totalTransacciones, double margenPromedio,
                           HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_" + tipoReporte + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");
        
        ServletOutputStream out = response.getOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte");
        
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont(workbook, true));
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(headerFont(workbook, false));
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        
        Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Reporte: " + getTituloReporte(tipoReporte));
        titleCell.setCellStyle(headerStyle);
        
        Row dateRow = sheet.createRow(1);
        org.apache.poi.ss.usermodel.Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        
        int kpiRow = 3;
        sheet.createRow(kpiRow++).createCell(0).setCellValue("RESUMEN");
        sheet.getRow(kpiRow - 1).getCell(0).setCellStyle(headerStyle);
        
        sheet.createRow(kpiRow++).createCell(0).setCellValue("Total Ventas: $" + String.format("%.2f", totalVentas));
        sheet.createRow(kpiRow++).createCell(0).setCellValue("Ganancia Neta: $" + String.format("%.2f", ganancia));
        sheet.createRow(kpiRow++).createCell(0).setCellValue("Transacciones: " + totalTransacciones);
        sheet.createRow(kpiRow++).createCell(0).setCellValue("Margen Promedio: " + String.format("%.2f", margenPromedio) + "%");
        
        int tableRow = kpiRow + 2;
        Row headerRow = sheet.createRow(tableRow++);
        String[] headers = {"Fecha", "Descripción", "Cantidad", "Precio Unit.", "Total", "Cliente/Prov.", "Método", "Ganancia"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (ReporteService.DatoReporte dato : datosReporte) {
            Row row = sheet.createRow(tableRow++);
            row.createCell(0).setCellValue(dato.getFecha() != null ? sdf.format(dato.getFecha()) : "");
            row.createCell(1).setCellValue(dato.getDescripcion());
            row.createCell(2).setCellValue(dato.getCantidad());
            row.createCell(3).setCellValue(dato.getPrecioUnitario());
            row.createCell(4).setCellValue(dato.getTotal());
            row.createCell(5).setCellValue(dato.getTercero());
            row.createCell(6).setCellValue(dato.getMetodo());
            row.createCell(7).setCellValue(dato.getGanancia());
            
            for (org.apache.poi.ss.usermodel.Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
        
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(out);
        workbook.close();
        out.close();
    }

    private Font headerFont(Workbook workbook, boolean bold) {
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(bold);
        return font;
    }

    private String getTituloReporte(String tipoReporte) {
        switch (tipoReporte) {
            case "ventas": return "Ventas por Período";
            case "compras": return "Compras por Período";
            case "rotacion": return "Rotación de Inventario";
            case "stockBajo": return "Productos con Stock Bajo";
            case "ganancias": return "Ganancias y Pérdidas";
            case "ventasCliente": return "Ventas por Cliente";
            default: return "Reporte General";
        }
    }
}
