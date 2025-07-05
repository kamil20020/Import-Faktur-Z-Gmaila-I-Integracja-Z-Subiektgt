package org.example.model.gmail.own;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
public class MessageAttachmentCombinedId {

    private final String messageId;
    private final Integer attachmentIndex;

    private static final String SEPARATOR = ",";

    public static MessageAttachmentCombinedId create(String value){

        if(value == null || value.isEmpty()){

            return null;
        }

        String[] values = value.split(SEPARATOR);

        if(values.length < 2){

            return null;
        }

        String messageId = values[0];
        Integer attachmentIndex = Integer.valueOf(values[1]);

        return new MessageAttachmentCombinedId(messageId, attachmentIndex);
    }

    @Override
    public String toString(){

        return messageId + "," + attachmentIndex;
    }

}
