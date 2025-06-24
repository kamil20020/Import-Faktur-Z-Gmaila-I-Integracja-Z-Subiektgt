package org.example.loader.pdf;

import java.util.List;

public record PdfLinesDetails(

    List<List<String>> lines,
    List<List<Float>> linesYCords,
    int numberOfPages
){}
