package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.api.sfera.request.CreateOrderRequest;
import org.example.external.gmail.Message;
import org.example.mapper.sfera.SferaOrderMapper;
import org.example.service.sfera.SferaOrderService;
import org.example.template.Template;
import org.example.template.data.TemplateCombinedData;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final TemplateService templateService;
    private final SferaOrderService sferaOrderService;

    public void createInvoices(Template template, Message message){

        template = templateService.loadTemplate("schemas/techpil.json");

        byte[] messageData = message.getAttachmentData();

        TemplateCombinedData templateCombinedData = templateService.applyTemplate(template, messageData);

        CreateOrderRequest request = SferaOrderMapper.map(templateCombinedData);

        sferaOrderService.create(request);
    }

    public void createInvoice(Template template, Message message){

        byte[] messageData = message.getAttachmentData();

        TemplateCombinedData templateCombinedData = templateService.applyTemplate(template, messageData);

        CreateOrderRequest request = SferaOrderMapper.map(templateCombinedData);

        sferaOrderService.create(request);
    }

}
