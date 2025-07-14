package org.example.gui.pdf_viewer;

import java.awt.image.BufferedImage;

public record PdfFileDetails(

    BufferedImage image,
    int numberOfPages
){}
