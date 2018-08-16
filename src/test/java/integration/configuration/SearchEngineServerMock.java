package integration.configuration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/images")
public class SearchEngineServerMock {

    private final PictureContentsRepository pictureContentsRepository;

    public SearchEngineServerMock() {
        pictureContentsRepository = PictureContentsRepository.getInstance();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPictureContent(PictureContentDto pictureContent) {
        pictureContentsRepository.add(pictureContent);
        return Response.status(201).entity(pictureContent).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PictureContentDto> getAllIndexedPictures() {
        return PictureContentsRepository.getInstance().fetchAll();
    }

    public static class PictureContentsRepository {
        private static PictureContentsRepository instance;
        private final List<PictureContentDto> pictureContentsIndex;

        private PictureContentsRepository() {
            pictureContentsIndex = new ArrayList<>();
        }

        static PictureContentsRepository getInstance() {
            if (instance == null) {
                instance = new PictureContentsRepository();
            }
            return instance;
        }

        void add(PictureContentDto pictureContent) {
            this.pictureContentsIndex.add(pictureContent);
        }

        List<PictureContentDto> fetchAll() {
            return pictureContentsIndex;
        }
    }

}