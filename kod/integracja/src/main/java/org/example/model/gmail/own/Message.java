package org.example.model.gmail.own;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String id;
    private String from;
    private OffsetDateTime date;
    private String subject;
    private String content;
    private List<String> attachmentsIds;

}
