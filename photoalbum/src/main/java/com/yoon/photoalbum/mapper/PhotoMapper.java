package com.yoon.photoalbum.mapper;

import com.yoon.photoalbum.domain.Photo;
import com.yoon.photoalbum.dto.PhotoDto;

public class PhotoMapper {

    public static PhotoDto convertToDto(Photo photo) {
        PhotoDto photoDto = new PhotoDto();
        photoDto.setPhotoId(photo.getPhotoId());
        photoDto.setFileName(photo.getFileName());
        photoDto.setFileSize(photo.getFileSize());
        photoDto.setOriginalUrl(photo.getOriginalUrl());
        photoDto.setThumbUrl(photo.getThumbUrl());
        photoDto.setUploadedAt(photo.getUploadedAt());
        photoDto.setAlbumId(photo.getAlbum().getAlbumId());
        return photoDto;
    }
}
