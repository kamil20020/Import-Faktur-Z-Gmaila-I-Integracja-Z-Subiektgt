package org.example.service.sfera;

import lombok.RequiredArgsConstructor;
import org.example.api.Api;
import org.example.api.sfera.SferaOrderApi;
import org.example.api.sfera.request.CreateOrderRequest;
import org.example.api.sfera.request.GetDocumentByExternalIdRequest;
import org.example.api.sfera.response.CreatedDocumentResponse;
import org.example.api.sfera.response.ErrorResponse;
import org.example.api.sfera.response.GeneralResponse;
import org.example.model.sfera.own.ResponseStatus;
import org.example.loader.JsonFileLoader;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SferaOrderService {

    private final SferaOrderApi sferaOrderApi;

    private GeneralResponse handleResponseErrors(HttpResponse<String> gotResponse) throws IllegalStateException{

        GeneralResponse generalResponse = Api.extractBody(gotResponse, GeneralResponse.class);

        if(generalResponse.getStatus().equals(ResponseStatus.ERROR.toString())){

            ErrorResponse errorResponse = Api.extractBody(gotResponse, ErrorResponse.class);

            throw new IllegalStateException(errorResponse.getMessage());
        }

        return generalResponse;
    }

    public int create(List<CreateOrderRequest> requests) {

        int numberOfSavedOrders = 0;

        for (CreateOrderRequest request : requests) {

            try {

                create(request);

                numberOfSavedOrders++;

            }
            catch (IllegalStateException e) {

                e.printStackTrace();
            }
        }

        return numberOfSavedOrders;
    }

    public String create(CreateOrderRequest createOrderRequest) throws IllegalStateException {

        HttpResponse<String> gotResponse = sferaOrderApi.create(createOrderRequest);

        GeneralResponse gotGeneralResponse = handleResponseErrors(gotResponse);

        CreatedDocumentResponse createdDocumentResponse = JsonFileLoader.loadFromStr(gotGeneralResponse.getData(), CreatedDocumentResponse.class);

        return createdDocumentResponse.getOrderExternalId();
    }

    public String getSubiektIdByExternalId(String orderExternalId) throws IllegalStateException{

        GetDocumentByExternalIdRequest request = new GetDocumentByExternalIdRequest(orderExternalId);

        HttpResponse<String> gotResponse = sferaOrderApi.getSubiektIdByExternalId(request);

        GeneralResponse generalResponse = handleResponseErrors(gotResponse);

        String gotSubiektId = generalResponse.getData();

        if(gotSubiektId.equals("null")){

            gotSubiektId = null;
        }

        return gotSubiektId;
    }

}
