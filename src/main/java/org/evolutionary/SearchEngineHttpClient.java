package org.evolutionary;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class SearchEngineHttpClient implements SearchEngine {

    private final WebResource.Builder webResourceBuilder;

    private SearchEngineHttpClient(WebResource.Builder webResourceBuilder) {
        this.webResourceBuilder = webResourceBuilder;
    }

    @Override
    public void index(PictureContent pictureContent) {
        webResourceBuilder.post(ClientResponse.class, pictureContent);
    }

    public static SearchEngineHttpClient createInstance(String resourceUrl) {
        return new SearchEngineHttpClient(createWebResourceBuilder(resourceUrl));
    }

    private static WebResource.Builder createWebResourceBuilder(String resourceUrl) {
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJaxbJsonProvider.class);
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        Client client = Client.create(config);
        WebResource resource = client.resource(resourceUrl);
        return resource
                .accept(APPLICATION_JSON)
                .type(APPLICATION_JSON);
    }
}