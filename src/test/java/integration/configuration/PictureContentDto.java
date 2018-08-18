package integration.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.evolutionary.domain.PictureContent;

@Data
@NoArgsConstructor
public class PictureContentDto {
    private String name;
    private String url;
    private String description;

    public PictureContent toPictureContent() {
        return new PictureContent(this.name, this.url, this.description);
    }
}