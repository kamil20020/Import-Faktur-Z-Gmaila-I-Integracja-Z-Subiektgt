package org.example.api.own;

import lombok.RequiredArgsConstructor;
import org.example.api.gmail.*;
import org.example.api.gmail.general.GmailBearerAuthApi;
import org.example.api.gmail.login.GmailLoginCodeApi;
import org.example.api.gmail.login.GmailLoginTokenApi;
import org.example.api.gmail.response.MessageDetails;
import org.example.external.gmail.Message;
import org.example.external.gmail.MessageHeader;
import org.example.service.MessageService;
import org.example.service.auth.GmailAuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartupConfiguration implements CommandLineRunner {

    private final GmailLoginCodeApi gmailLoginCodeApi;
    private final GmailLoginTokenApi gmailLoginTokenApi;
    private final GmailMessageApi gmailMessageApi;

    private final GmailAuthService gmailAuthService;
    private final MessageService messageService;

    @Override
    public void run(String... args) throws Exception {

//        if(!gmailAuthService.isUserLogged()){
//
//            gmailAuthService.generateCode();
//        }
//
//        List<MessageHeader> messageHeaders = messageService.getPage(2).messageHeaders();
//
//        List<Message> messages = new ArrayList<>();
//
//        for(MessageHeader messageHeader : messageHeaders){
//
//            String messageId = messageHeader.id();
//
//            Message message = messageService.getMessageById(messageId);
//
//            messages.add(message);
//        }
//
//        for(Message message : messages){
//
//            System.out.println(message);
//        }
    }

}
