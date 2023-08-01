package com.yoon.photoalbum.mapper;

import com.yoon.photoalbum.domain.Album;
import com.yoon.photoalbum.dto.AlbumDto;

public class AlbumMapper {

    public static AlbumDto convertToDto(Album album) {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumId(album.getAlbumId());
        albumDto.setAlbumName(album.getAlbumName());
        albumDto.setCreatedAt(album.getCreatedAt());
        return albumDto;
    }
}
