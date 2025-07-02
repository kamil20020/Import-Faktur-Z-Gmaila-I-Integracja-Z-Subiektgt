package org.example.service.unit.sfera;

import org.example.TestHttpResponse;
import org.example.api.Api;
import org.example.api.sfera.SferaOrderApi;
import org.example.api.sfera.request.CreateOrderRequest;
import org.example.api.sfera.request.GetDocumentByExternalIdRequest;
import org.example.api.sfera.response.CreatedDocumentResponse;
import org.example.api.sfera.response.GeneralResponse;
import org.example.external.sfera.own.ResponseStatus;
import org.example.loader.JsonFileLoader;
import org.example.service.sfera.SferaOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SferaOrderServiceTest {

    @Mock
    private SferaOrderApi sferaOrderApi;

    @InjectMocks
    private SferaOrderService sferaOrderService;

    @Test
    void shouldCreate() {

        //given
        TestHttpResponse expectedResponse = TestHttpResponse.builder()
            .build();

        GeneralResponse expectedGeneralResponse = GeneralResponse.builder()
            .status(ResponseStatus.SUCCESS.toString())
            .data("data")
            .build();

        CreatedDocumentResponse expectedCreatedDocumentResponse = CreatedDocumentResponse.builder()
            .orderExternalId("external-id")
            .build();

        CreateOrderRequest request = new CreateOrderRequest();

        //when
        Mockito.when(sferaOrderApi.create(any())).thenReturn(expectedResponse);

        try(
            MockedStatic<Api> apiMock = Mockito.mockStatic(Api.class);
            MockedStatic<JsonFileLoader> jsonFileLoaderMock = Mockito.mockStatic(JsonFileLoader.class);
        ){

            apiMock.when(() -> Api.extractBody(any(), any())).thenReturn(expectedGeneralResponse);
            jsonFileLoaderMock.when(() -> JsonFileLoader.loadFromStr(any(), any(Class.class))).thenReturn(expectedCreatedDocumentResponse);

            String gotExternalId = sferaOrderService.create(request);

            //then
            assertNotNull(gotExternalId);
            assertEquals(expectedCreatedDocumentResponse.getOrderExternalId(), gotExternalId);

            apiMock.verify(() -> Api.extractBody(expectedResponse, GeneralResponse.class));
            jsonFileLoaderMock.verify(() -> JsonFileLoader.loadFromStr(expectedGeneralResponse.getData(), CreatedDocumentResponse.class));
        }

        Mockito.verify(sferaOrderApi).create(request);
    }

    @Test
    void shouldCreateFromList() {

        //given
        CreateOrderRequest createOrderRequest1 = new CreateOrderRequest();
        CreateOrderRequest createOrderRequest2 = new CreateOrderRequest();

        List<CreateOrderRequest> requests = List.of(createOrderRequest1, createOrderRequest2);

        int expectedNumberOfCreatedOrders = requests.size();

        TestHttpResponse expectedResponse = TestHttpResponse.builder()
            .build();

        GeneralResponse expectedGeneralResponse = GeneralResponse.builder()
            .status(ResponseStatus.SUCCESS.toString())
            .data("data")
            .build();

        CreatedDocumentResponse expectedCreatedDocumentResponse = CreatedDocumentResponse.builder()
            .orderExternalId("external-id")
            .build();

        //when
        Mockito.when(sferaOrderApi.create(any())).thenReturn(expectedResponse);

        try(
            MockedStatic<Api> apiMock = Mockito.mockStatic(Api.class);
            MockedStatic<JsonFileLoader> jsonFileLoaderMock = Mockito.mockStatic(JsonFileLoader.class);
        ){

            apiMock.when(() -> Api.extractBody(any(), any())).thenReturn(expectedGeneralResponse);
            jsonFileLoaderMock.when(() -> JsonFileLoader.loadFromStr(any(), any(Class.class))).thenReturn(expectedCreatedDocumentResponse);

            int gotNumberOfCreatedOrders = sferaOrderService.create(requests);

            //then
            assertEquals(expectedNumberOfCreatedOrders, gotNumberOfCreatedOrders);
        }

        for(CreateOrderRequest createOrderRequest : requests){

            Mockito.verify(sferaOrderApi, Mockito.times(requests.size())).create(createOrderRequest);
        }
    }

    @Test
    void shouldGetSubiektIdByExternalId() {

        //given
        String orderId = "order-id";
        String expectedSubiektId = "subiekt-id";

        TestHttpResponse expectedResponse = TestHttpResponse.builder()
            .build();

        GeneralResponse expectedGeneralResponse = GeneralResponse.builder()
            .status(ResponseStatus.SUCCESS.toString())
            .data(expectedSubiektId)
            .build();

        //when
        Mockito.when(sferaOrderApi.getSubiektIdByExternalId(any())).thenReturn(expectedResponse);

        try(
            MockedStatic<Api> apiMock = Mockito.mockStatic(Api.class);
        ){

            apiMock.when(() -> Api.extractBody(any(), any())).thenReturn(expectedGeneralResponse);

            String gotSubiektId = sferaOrderService.getSubiektIdByExternalId(orderId);

            //then
            assertNotNull(gotSubiektId);
            assertEquals(expectedSubiektId, gotSubiektId);

            apiMock.verify(() -> Api.extractBody(expectedResponse, GeneralResponse.class));
        }

        //then
        ArgumentCaptor<GetDocumentByExternalIdRequest> requestCaptor = ArgumentCaptor.forClass(GetDocumentByExternalIdRequest.class);

        Mockito.verify(sferaOrderApi).getSubiektIdByExternalId(requestCaptor.capture());

        GetDocumentByExternalIdRequest gotRequest = requestCaptor.getValue();

        assertNotNull(gotRequest);
        assertEquals(orderId, gotRequest.getExternalId());
    }

}