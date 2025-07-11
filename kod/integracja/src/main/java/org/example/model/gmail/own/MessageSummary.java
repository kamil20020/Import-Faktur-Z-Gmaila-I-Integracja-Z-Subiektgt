package org.example.model.gmail.own;

import java.util.List;

public record MessageSummary(

    String id,
    String from,
    String date,
    String subject,
    String content,
    List<String> attachmentsIds
){}
