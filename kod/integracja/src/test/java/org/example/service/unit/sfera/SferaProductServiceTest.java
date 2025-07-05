package org.example.service.unit.sfera;

import org.example.TestHttpResponse;
import org.example.api.Api;
import org.example.api.sfera.SferaProductApi;
import org.example.api.sfera.request.GetProductByCodeAndEanRequest;
import org.example.api.sfera.response.GeneralResponse;
import org.example.model.sfera.own.ResponseStatus;
import org.example.service.sfera.SferaProductService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SferaProductServiceTest {

    @Mock
    private SferaProductApi sferaProductApi;

    @InjectMocks
    private SferaProductService sferaProductService;

    @ParameterizedTest
    @CsvSource(value = {
        "subiekt-id, subiekt-id",
        "null, "
    })
    void shouldGetSubiektIdByCodeOrEanWhenWasFound(String expectedGotSubiektId, String expectedSubiektId) {

        //given
        String code = "code";
        String ean = "ean";

        GeneralResponse expectedGeneralResponse = GeneralResponse.builder()
            .status(ResponseStatus.SUCCESS.toString())
            .data(expectedGotSubiektId)
            .build();

        TestHttpResponse expectedResponse = TestHttpResponse.builder()
            .build();

        //when
        Mockito.when(sferaProductApi.getSubiektIdByCodeAndEan(any())).thenReturn(expectedResponse);

        try(
            MockedStatic<Api> apiMock = Mockito.mockStatic(Api.class);
        ){

            apiMock.when(() -> Api.extractBody(any(), any())).thenReturn(expectedGeneralResponse);

            String gotSubiektId = sferaProductService.getSubiektIdByCodeOrEan(code, ean);

            //then
            assertEquals(expectedSubiektId, gotSubiektId);

            apiMock.verify(() -> Api.extractBody(expectedResponse, GeneralResponse.class));
        }

        ArgumentCaptor<GetProductByCodeAndEanRequest> requestCaptor = ArgumentCaptor.forClass(GetProductByCodeAndEanRequest.class);

        Mockito.verify(sferaProductApi).getSubiektIdByCodeAndEan(requestCaptor.capture());

        GetProductByCodeAndEanRequest gotRequest = requestCaptor.getValue();

        assertNotNull(gotRequest);
        assertEquals(code, gotRequest.getCode());
        assertEquals(ean, gotRequest.getEan());
    }

}