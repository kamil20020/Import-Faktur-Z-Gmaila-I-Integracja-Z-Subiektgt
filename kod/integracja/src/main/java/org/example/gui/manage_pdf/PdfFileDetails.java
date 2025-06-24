package org.example.gui.manage_pdf;

import java.awt.image.BufferedImage;

public record PdfFileDetails(

    BufferedImage image,
    int numberOfPages
){}
