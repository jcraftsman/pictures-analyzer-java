package org.evolutionary.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class File {
    private final String name;
    private final String path;
}
