package org.example.external.gmail;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private String externalId;

    public void setExternalId(String externalId){

        this.externalId = externalId;

        if(externalId == null){
            return;
        }

        this.externalId = this.externalId
            .replaceAll("\"", "");
    }


}
