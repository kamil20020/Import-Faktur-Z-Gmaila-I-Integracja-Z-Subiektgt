package org.example.external.gmail;

public record MessageSummary(

    String id,
    String from,
    String date,
    String subject,
    String attachmentId
){}
