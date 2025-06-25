package org.example.api.sfera;

import org.example.api.sfera.request.GeneralRequest;
import org.example.api.sfera.request.GetProductByCodeAndEanRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class SferaProductApi extends SferaApi{

    public SferaProductApi() {

        super("product");
    }

    public HttpResponse<String> getSubiektIdByCodeAndEan(GetProductByCodeAndEanRequest request){

        GeneralRequest generalRequest = createGeneralRequest(request);

        String requestStr = handleMapRequestToString(generalRequest);

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(requestStr))
            .uri(URI.create(API_PREFIX + "/getSymbolByCodeOrEan"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/pdf");

        return send(httpRequestBuilder);
    }

}
