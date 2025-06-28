package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.api.Api;
import org.example.api.sfera.request.CreateOrderRequest;
import org.example.exception.FileReadException;
import org.example.external.gmail.Message;
import org.example.mapper.sfera.SferaOrderMapper;
import org.example.service.sfera.SferaOrderService;
import org.example.template.data.DataExtractedFromTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final TemplateService templateService;
    private final SferaOrderService sferaOrderService;

    private static final Logger log = LoggerFactory.getLogger(Api.class);

    public void createInvoices(List<Message> messages){

        for(Message message : messages){

            try{

                createInvoice(message);
            }
            catch (FileReadException | IllegalStateException e){

                e.printStackTrace();

                log.error(e.getMessage());
            }
        }
    }

    private void createInvoice(Message message){

        byte[] messageData = message.getAttachmentData();

        DataExtractedFromTemplate dataExtractedFromTemplate;

        dataExtractedFromTemplate = templateService.applyGoodTemplateForData(messageData);

        CreateOrderRequest request = SferaOrderMapper.map(dataExtractedFromTemplate);

        sferaOrderService.create(request);
    }

}
