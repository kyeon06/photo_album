package com.yoon.photoalbum.service;

import com.yoon.photoalbum.Constants;
import com.yoon.photoalbum.domain.Album;
import com.yoon.photoalbum.domain.Photo;
import com.yoon.photoalbum.dto.AlbumDto;
import com.yoon.photoalbum.dto.DeleteRequestBody;
import com.yoon.photoalbum.dto.MoveRequestBody;
import com.yoon.photoalbum.dto.PhotoDto;
import com.yoon.photoalbum.mapper.PhotoMapper;
import com.yoon.photoalbum.repository.AlbumRepository;
import com.yoon.photoalbum.repository.PhotoRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AlbumRepository albumRepository;

    private final String original_path = Constants.PATH_PREFIX + "/photos/original";
    private final String thumb_path = Constants.PATH_PREFIX + "/photos/thumb";

    // 사진 상세정보 조회
    public PhotoDto getPhoto(long photoId) {
        Optional<Photo> res = photoRepository.findById(photoId);

        if (res.isPresent()) {
            PhotoDto photoDto = PhotoMapper.convertToDto(res.get());
            return photoDto;
        } else {
            throw new EntityNotFoundException(String.format("사진 ID '%d'로 조회되지 않습니다.", photoId));
        }
    }

    // 같은 파일명이 있는지 체크해서 파일명 변경해주는 method
    private String checkFileName(String fileName, Long albumId) {
        String fileNameNoExt = StringUtils.stripFilenameExtension(fileName);
        String ext = StringUtils.getFilenameExtension(fileName);

        Optional<Photo> res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);

        int count = 2;
        while (res.isPresent()) {
            fileName = String.format("%s (%d).%s", fileNameNoExt, count, ext);
            res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);
            count++;
        }

        return fileName;
    }

    // 이미지 저장
    private void saveFile(MultipartFile file, Long albumId, String fileName) throws IOException {
        try {
            String filePath = albumId + "/" + fileName;
            Files.copy(file.getInputStream(), Paths.get(original_path + "/" + filePath));

            // 원본 -> 썸네일 resize
            BufferedImage thumbImg = Scalr.resize(ImageIO.read(file.getInputStream()), Constants.THUMB_SIZE, Constants.THUMB_SIZE);

            // 썸네일 이미지 저장
            File thumbFile = new File(thumb_path + "/" + filePath);
            String ext = StringUtils.getFilenameExtension(fileName);
            if (ext == null) {
                throw new IllegalArgumentException("No Extention");
            }
            ImageIO.write(thumbImg, ext, thumbFile);
        } catch (Exception e) {
            throw new RuntimeException("이미지를 저장할 수 없습니다. Error : " + e.getMessage());
        }
    }

    // 사진 업로드
    public PhotoDto savePhoto(MultipartFile file, Long albumId) throws IOException {
        Optional<Album> album = albumRepository.findById(albumId);
        // 앨범이 존재하는지 확인
        if (album.isEmpty()){
            throw new EntityNotFoundException("앨범이 존재하지 않습니다.");
        }
        // 파일명, 파일용량 추출
        String fileName = file.getOriginalFilename();
        int fileSize = (int)file.getSize();
        fileName = checkFileName(fileName, albumId);
        saveFile(file, albumId, fileName);

        // 사진 저장
        Photo photo = new Photo();
        photo.setOriginalUrl("/photos/original/" + albumId + "/" + fileName);
        photo.setThumbUrl("/photos/thumb/" + albumId + "/" + fileName);
        photo.setFileName(fileName);
        photo.setFileSize(fileSize);
        photo.setAlbum(album.get());
        Photo createdPhoto = photoRepository.save(photo);

        return PhotoMapper.convertToDto(createdPhoto);
    }

    // 사진 다운로드
    public File getImageFile(Long photoId) {
        Optional<Photo> res = photoRepository.findById(photoId);
        if (res.isEmpty()) {
            throw new EntityNotFoundException(String.format("사진 ID %d를 찾을 수 없습니다.", photoId));
        }

        return new File(Constants.PATH_PREFIX + res.get().getOriginalUrl());
    }

    // 사진 목록 불러오기
    public List<PhotoDto> getPhotoList(Long albumId, String sort, String keyword) {
        Optional<Album> album = albumRepository.findById(albumId);
        List<Photo> photos;

        if (Objects.equals(sort, "byName")) {
            photos = photoRepository.findByAlbumAndFileNameContainingOrderByFileNameAsc(album.get(), keyword);
        } else if (Objects.equals(sort, "byDate")) {
            photos = photoRepository.findByAlbumAndFileNameContainingOrderByUploadedAtDesc(album.get(), keyword);
        } else {
            throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");
        }

        List<PhotoDto> photoDtos = PhotoMapper.convertToDtoList(photos);

        return photoDtos;
    }

    // 사진 앨범 옮기기
    public List<PhotoDto> movePhoto(MoveRequestBody moveRequestBody) {
        Optional<Album> fromAlbum = albumRepository.findById(moveRequestBody.getFromAlbumId());
        Optional<Album> toAlbum = albumRepository.findById(moveRequestBody.getToAlbumId());

        if (fromAlbum.isEmpty()) {
            throw new NoSuchElementException(String.format("앨범 ID %d를 찾을 수 없습니다.", moveRequestBody.getFromAlbumId()));
        }
        if (toAlbum.isEmpty()) {
            throw new NoSuchElementException(String.format("앨범 ID %d를 찾을 수 없습니다.", moveRequestBody.getToAlbumId()));
        }

        // 앨범에 해당하는 사진 목록 불러오기
        List<Photo> photos = photoRepository.findByAlbum_AlbumId(moveRequestBody.getFromAlbumId());
        List<Long> movePhotoIds = List.of(moveRequestBody.getPhotoIds());

        // 앨범 변경
        for (Photo photo : photos) {
            if (movePhotoIds.contains(photo.getPhotoId())) {
                photo.setAlbum(toAlbum.get());
                photoRepository.save(photo);
            }
        }

        // 제외한 목록 다시 불러오기
        List<Photo> resphotos = photoRepository.findByAlbum_AlbumId(moveRequestBody.getFromAlbumId());
        return PhotoMapper.convertToDtoList(resphotos);
    }

    // 사진 삭제
    public List<PhotoDto> deletePhoto(Long albumId, DeleteRequestBody requestBody) {
        Optional<Album> album = albumRepository.findById(albumId);
        if (album.isEmpty()) {
            throw new NoSuchElementException(String.format("앨범 ID %d를 찾을 수 없습니다.", albumId));
        }

        // 앨범 내 사직 목록
        List<Photo> photos = photoRepository.findByAlbum_AlbumId(albumId);
        // 삭제할 사진 목록 ID
        List<Long> deletePhotoIds = List.of(requestBody.getPhotoIds());

        for (Photo photo : photos) {
            if (deletePhotoIds.contains(photo.getPhotoId())) {
                photoRepository.delete(photo);
            }
        }

        List<Photo> resPhotos = photoRepository.findByAlbum_AlbumId(albumId);
        return PhotoMapper.convertToDtoList(resPhotos);
    }
}
