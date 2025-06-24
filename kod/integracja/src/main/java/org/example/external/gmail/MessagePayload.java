package org.example.external.gmail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "headers",
    "parts"
})
public record MessagePayload(

    @JsonProperty("headers")
    List<MessagePayloadHeader> messagePayloadHeaders,

    @JsonProperty("parts")
    List<MessageContentPart> messageContentParts
){

    public String getHeaderValue(String headerName){

        Optional<MessagePayloadHeader> gotPayloadHeaderOpt = messagePayloadHeaders.stream()
            .filter(header -> header.name().equals(headerName))
            .findFirst();

        if(gotPayloadHeaderOpt.isEmpty()){

            return "";
        }

        MessagePayloadHeader messagePayloadHeader = gotPayloadHeaderOpt.get();

        return messagePayloadHeader.value();
    }

    public String getAttachmentId(String fileExtension){

        for(MessageContentPart messageContentPart : messageContentParts){

            String filename = messageContentPart.filename();
            MessageContentPartBody body = messageContentPart.body();

            if(filename == null || body == null){
                continue;
            }

            String attachmentId = body.attachmentId();

            if(!filename.endsWith(fileExtension) || attachmentId == null){
                continue;
            }

            return attachmentId;
        }


        return null;
    }

}
