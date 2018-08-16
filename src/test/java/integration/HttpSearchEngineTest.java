package integration;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import integration.configuration.PictureContentDto;
import integration.configuration.SearchEngineServerMock;
import org.evolutionary.HttpSearchEngine;
import org.evolutionary.PictureContent;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class HttpSearchEngineTest {

    private static final String BASE_URI = "http://localhost:8080/api";
    private static final String RESOURCE_URL = BASE_URI + "/images";
    private static final String PICTURE_NAME = "picture.jpeg";
    private static final String PICTURE_URL = "/path/to/my/file.jpeg";
    private static final String PICTURE_DESCRIPTION = "this text is contained in the image";

    private static HttpServer server;

    private HttpSearchEngine httpSearchEngine;

    @BeforeAll
    static void initAll() {
        final ResourceConfig resourceConfig = new ResourceConfig(SearchEngineServerMock.class);
        resourceConfig.register(JacksonFeature.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resourceConfig);
    }

    @BeforeEach
    void setUp() {
        httpSearchEngine = HttpSearchEngine.createInstance(RESOURCE_URL);
    }

    @AfterAll
    static void tearDownAll() {
        server.shutdownNow();
    }

    @Test
    void should_index_picture_content_in_search_engine() {
        // Given
        PictureContent pictureContentToIndex = new PictureContent(PICTURE_NAME, PICTURE_URL, PICTURE_DESCRIPTION);

        // When
        httpSearchEngine.index(pictureContentToIndex);

        // Then
        assertThat(getAllPictureContentsFromSearchEngine()).contains(pictureContentToIndex);
    }

    private List<PictureContent> getAllPictureContentsFromSearchEngine() {
        Client client = Client.create();
        WebResource resource = client.resource(RESOURCE_URL);
        ClientResponse clientResponse = resource.get(ClientResponse.class);
        PictureContentDto[] pictureContentDtos = clientResponse.getEntity(PictureContentDto[].class);
        return Stream.of(pictureContentDtos)
                .map(PictureContentDto::toPictureContent)
                .collect(toList());
    }

}