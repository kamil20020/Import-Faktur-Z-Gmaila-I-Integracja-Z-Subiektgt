package org.example.model.gmail.own;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageAttachment {

    private String id;
    private MessageAttachmentCombinedId combinedId;

    @ToString.Exclude
    private Message message;

    private Integer index;

    @ToString.Exclude
    private byte[] data;

    @JsonIgnore
    private String externalId;

    public MessageAttachment(Message message, String attachmentId, Integer attachmentIndex){

        String messageId = message.getId();

        this.id = attachmentId;
        this.combinedId = new MessageAttachmentCombinedId(messageId, attachmentIndex);
        this.message = message;
        this.index = attachmentIndex;
    }

    public void setExternalId(String value){

        this.externalId = value;

        if(value == null){

            return;
        }

        this.externalId = externalId
            .replaceAll("\"", "");
    }

    public String getMessageId(){

        if(combinedId == null){

            return null;
        }

        return combinedId.getMessageId();
    }

    public Integer getAttachmentIndex(){

        if(combinedId == null){

            return null;
        }

        return combinedId.getAttachmentIndex();
    }

}
