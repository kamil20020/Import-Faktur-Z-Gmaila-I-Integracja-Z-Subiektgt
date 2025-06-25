package org.example.api.sfera;

import org.example.api.sfera.request.CreateOrderRequest;
import org.example.api.sfera.request.GeneralRequest;
import org.example.api.sfera.request.GetDocumentByExternalIdRequest;
import org.example.api.sfera.request.GetOrderRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class SferaOrderApi extends SferaApi{

    public SferaOrderApi() {

        super("order");
    }

    public HttpResponse<String> create(CreateOrderRequest createOrderRequest)  {

        GeneralRequest generalRequest = createGeneralRequest(createOrderRequest);

        String requestStr = handleMapRequestToString(generalRequest);

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(requestStr))
            .uri(URI.create(API_PREFIX + "/add"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json");

        return send(httpRequestBuilder);
    }

    public HttpResponse<String> getSubiektIdByExternalId(GetDocumentByExternalIdRequest request){

        GeneralRequest generalRequest = createGeneralRequest(request);

        String requestStr = handleMapRequestToString(generalRequest);

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(requestStr))
            .uri(URI.create(API_PREFIX + "/getNrByExternalId"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json");

        return send(httpRequestBuilder);
    }

    public HttpResponse<String> getDocumentContent(GetOrderRequest getOrderRequest)  {

        GeneralRequest generalRequest = createGeneralRequest(getOrderRequest);

        String requestStr = handleMapRequestToString(generalRequest);

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(requestStr))
            .uri(URI.create(API_PREFIX + "/getpdf"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/pdf");

        return send(httpRequestBuilder);
    }

}
