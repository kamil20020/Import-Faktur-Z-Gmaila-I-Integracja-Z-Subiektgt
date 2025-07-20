package org.example.model.gmail.generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.example.mapper.gmail.GmailDataMapper;

import java.util.*;

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

    private static final String MESSAGE_CONTENT_PARENT_TYPE = "multipart/alternative";
    private static final String MESSAGE_CONTENT_TYPE = "text/plain";

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

    public String getContent(){

        for(MessageContentPart messageContentPart : messageContentParts){

            String gotContent = getContent(messageContentPart);

            if(gotContent != null){

                return gotContent;
            }
        }

        return null;
    }

    public static String getContent(MessageContentPart parentPart){

        if(parentPart == null) {

            return null;
        }

        String gotContent = getMessageContentPartContent(parentPart);

        if(gotContent != null){

            return gotContent;
        }

        List<MessageContentPart> subParts = parentPart.parts();

        if(subParts == null || subParts.isEmpty()){

            return null;
        }

        for(MessageContentPart messageContentPart : subParts){

            gotContent = getMessageContentPartContent(messageContentPart);

            if(gotContent != null){

                return gotContent;
            }
        }

        return null;
    }

    public static String getMessageContentPartContent(MessageContentPart messageContentPart){

        if(messageContentPart == null){

            return null;
        }

        String mimeType = messageContentPart.mimeType();

        if(!Objects.equals(mimeType, MESSAGE_CONTENT_TYPE)) {

            return null;
        }

        MessageContentPartBody body = messageContentPart.body();

        if(body == null){

            return null;
        }

        String gotEncodedContent = body.getData();

        if(gotEncodedContent == null || gotEncodedContent.isEmpty()){

            return null;
        }

        byte[] gotDecodedContent = GmailDataMapper.decode(gotEncodedContent);

        return new String(gotDecodedContent);
    }

}
