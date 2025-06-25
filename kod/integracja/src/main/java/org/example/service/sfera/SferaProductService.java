package org.example.service.sfera;

import lombok.RequiredArgsConstructor;
import org.example.api.Api;
import org.example.api.sfera.SferaProductApi;
import org.example.api.sfera.request.GetProductByCodeAndEanRequest;
import org.example.api.sfera.response.ErrorResponse;
import org.example.api.sfera.response.GeneralResponse;
import org.example.external.sfera.generated.ResponseStatus;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SferaProductService {

    private final SferaProductApi sferaProductApi;

    private GeneralResponse handleResponseErrors(HttpResponse<String> gotResponse) throws IllegalStateException{

        GeneralResponse generalResponse = Api.extractBody(gotResponse, GeneralResponse.class);

        if(generalResponse.getStatus().equals(ResponseStatus.ERROR.toString())){

            ErrorResponse errorResponse = Api.extractBody(gotResponse, ErrorResponse.class);

            throw new IllegalStateException(errorResponse.getMessage());
        }

        return generalResponse;
    }

    public String getSubiektIdByCodeOrEan(String code, String ean) throws IllegalStateException{

        GetProductByCodeAndEanRequest request = new GetProductByCodeAndEanRequest(code, ean);

        HttpResponse<String> gotResponse = sferaProductApi.getSubiektIdByCodeAndEan(request);

        GeneralResponse generalResponse = handleResponseErrors(gotResponse);

        String gotSubiektId = generalResponse.getData();

        if(gotSubiektId.equals("null")){

            gotSubiektId = null;
        }

        return gotSubiektId;
    }

}
