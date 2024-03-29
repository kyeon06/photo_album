package com.yoon.photoalbum.mapper;

import com.yoon.photoalbum.domain.Photo;
import com.yoon.photoalbum.dto.PhotoDto;

import java.util.List;
import java.util.stream.Collectors;

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

    public static Photo convertToModel(PhotoDto photoDto) {
        Photo photo = new Photo();
        photo.setPhotoId(photoDto.getPhotoId());
        photo.setFileName(photoDto.getFileName());
        photo.setFileSize(photoDto.getFileSize());
        photo.setOriginalUrl(photoDto.getOriginalUrl());
        photo.setThumbUrl(photoDto.getThumbUrl());
        photo.setUploadedAt(photoDto.getUploadedAt());

        return photo;
    }

    public static List<PhotoDto> convertToDtoList(List<Photo> photos) {
        return photos.stream().map(PhotoMapper::convertToDto).collect(Collectors.toList());
    }
}
