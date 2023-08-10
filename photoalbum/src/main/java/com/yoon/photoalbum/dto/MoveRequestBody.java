package com.yoon.photoalbum.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveRequestBody {
    private Long fromAlbumId;
    private Long toAlbumId;
    private Long[] photoIds;
}
