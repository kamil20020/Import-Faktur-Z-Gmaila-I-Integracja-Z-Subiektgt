package org.example.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.loader.JsonFileLoader;
import org.example.service.PropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public abstract class Api {

    protected String API_PREFIX;

    private final PropertiesService propertiesService;

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Logger log = LoggerFactory.getLogger(Api.class);

    public Api(String subDomain, String laterPrefix, String hostPropertyName, String protocol, PropertiesService propertiesService){

        this.propertiesService = propertiesService;

        if(subDomain != null && !subDomain.isBlank()){
            subDomain += '.';
        }

        API_PREFIX =  protocol + "://" + (subDomain != null ? subDomain : "") + getEnvApiHost(hostPropertyName) + laterPrefix;
    }

    public Api(String subDomain, String laterPrefix, String hostKey, PropertiesService propertiesService){

        this(subDomain, laterPrefix, hostKey, "https", propertiesService);
    }

    public Api(String laterPrefix, String hostKey, PropertiesService propertiesService){

        this(null, laterPrefix, hostKey, propertiesService);
    }

    protected static String getQueryParamsPostFix(String... titlesAndParams){

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("?");

        for(int i = 0; i < titlesAndParams.length - 1; i += 2){

            if(i > 0){

                stringBuilder.append("&");
            }

            stringBuilder.append(titlesAndParams[i]);
            stringBuilder.append("=");

            String value = titlesAndParams[i + 1];

            value = URLEncoder.encode(value, StandardCharsets.UTF_8);

            stringBuilder.append(value);
        }

        return stringBuilder.toString();
    }

    private String getEnvApiHost(String hostPropertyName){

        return propertiesService.getProperty(hostPropertyName, String.class);
    }

    public HttpResponse<String> send(HttpRequest.Builder httpRequestBuilder) throws IllegalStateException{

        HttpRequest httpRequest = httpRequestBuilder.build();

        log.info("Request: ");
        log.info("Url: " + httpRequest.uri());
        log.info("Authorization: " + httpRequest.headers().firstValue("Authorization").orElse("") + "\n");

        try {
            HttpResponse<String> gotResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            log.info("Response: ");
            log.info("Status code: " + gotResponse.statusCode());
            log.info("Body: " + gotResponse.body());

            return gotResponse;
        }
        catch (IOException | InterruptedException e) {

            e.printStackTrace();

            throw new IllegalStateException(e.getMessage());
        }
    }

    public static void redirect(String url, String... params){

        URI uri = URI.create(
            url + getQueryParamsPostFix(params)
        );

        try {
            Desktop.getDesktop().browse(uri);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T extractBody(HttpResponse<String> httpResponse, Class<T> type){

        T gotConvertedBody = JsonFileLoader.loadFromStr(httpResponse.body(), type);

        log.info(gotConvertedBody.toString());

        return gotConvertedBody;
    }

    protected static String handleMapRequestToString(Object object) throws IllegalStateException{

        try {

            return objectMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {

            e.printStackTrace();

            throw new IllegalStateException(e.getMessage());
        }
    }

}
