package com.studyhub.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.studyhub.model.Asignacion;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExportService {
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    public ByteArrayInputStream generarPdf(List<Asignacion> asignaciones) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph para = new Paragraph("Cronograma Semanal - StudyHub", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            for (Asignacion a : asignaciones) {
                document.add(new Paragraph("Fecha: " + a.getFecha() + 
                                         " | Proyecto: " + a.getProyecto() + 
                                         " | Usuario: " + a.getUsuario().getNombre() + 
                                         " | Turno: " + a.getTurno().getNombre()));
            }

            document.close();
        } catch (DocumentException ex) {
            logger.error("Error al generar PDF: ", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public String generarCsv(List<Asignacion> asignaciones) {
        StringBuilder sb = new StringBuilder();
        sb.append("Fecha,Proyecto,Usuario,Turno,Horas,Conflicto\n");
        for (Asignacion a : asignaciones) {
            sb.append(a.getFecha()).append(",")
              .append(a.getProyecto()).append(",")
              .append(a.getUsuario().getNombre()).append(",")
              .append(a.getTurno().getNombre()).append(",")
              .append(a.getHorasDiarias()).append(",")
              .append(a.getTieneConflicto()).append("\n");
        }
        return sb.toString();
    }
}
