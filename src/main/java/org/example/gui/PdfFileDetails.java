package org.example.gui;

import java.awt.image.BufferedImage;

public record PdfFileDetails(

    BufferedImage image,
    int numberOfPages
){}
