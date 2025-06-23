package org.example.external.gmail;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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

    @ToString.Exclude
    private byte[] attachmentData;

}
