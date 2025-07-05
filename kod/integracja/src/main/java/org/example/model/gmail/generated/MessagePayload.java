package org.example.model.gmail.generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
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

    public List<String> getAttachmentsIds(String fileExtension){

        List<String> attachmentsIds = new ArrayList<>();

        Integer fileExtensionLength = fileExtension.length();

        for(MessageContentPart messageContentPart : messageContentParts){

            String filename = messageContentPart.filename();
            MessageContentPartBody body = messageContentPart.body();

            if(filename == null || filename.length() < fileExtensionLength || body == null){
                continue;
            }

            String attachmentId = body.getAttachmentId();

            String gotFileExtension = filename
                .substring(filename.length() - fileExtensionLength)
                .toLowerCase();

            if(!gotFileExtension.equals(fileExtension) || attachmentId == null){
                continue;
            }

            attachmentsIds.add(attachmentId);
        }

        return attachmentsIds;
    }

}
