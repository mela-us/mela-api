package com.hcmus.mela.shared.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.cos.COSName;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PdfExtractor {

    public static class ExtractedPdf {
        public String text;
        public List<String> imageBase64 = new ArrayList<>();
    }

    public static ExtractedPdf extractFromUrl(String fileUrl, Integer startPage, Integer endPage) throws IOException {
        URL url = new URL(fileUrl);
        try (InputStream in = url.openStream();
             PDDocument doc = Loader.loadPDF(in.readAllBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);

            String text = stripper.getText(doc);

            List<String> images64 = new ArrayList<>();
            PDPageTree pages = doc.getPages();
            for (int i = startPage - 1; i < endPage && i < pages.getCount(); i++) {
                PDPage page = pages.get(i);
                PDResources resources = page.getResources();
                for (COSName name : resources.getXObjectNames()) {
                    var xo = resources.getXObject(name);
                    if (xo instanceof PDImageXObject imgX) {
                        BufferedImage img = imgX.getImage();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(img, "png", baos);
                        String b64 = Base64.getEncoder().encodeToString(baos.toByteArray());
                        images64.add("data:image/png;base64," + b64);
                    }
                }
            }

            ExtractedPdf result = new ExtractedPdf();
            result.text = text;
            result.imageBase64 = images64;
            return result;
        }
    }

}
