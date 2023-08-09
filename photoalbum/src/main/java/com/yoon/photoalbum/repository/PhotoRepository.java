package com.yoon.photoalbum.repository;

import com.yoon.photoalbum.domain.Album;
import com.yoon.photoalbum.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    int countByAlbum_AlbumId(Long albumId);

    // 앨범아이디별 최신 4장의 이미지를 가져오는 method
    List<Photo> findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(Long albumId);

    // 같은 파일명이 존재하는지 체크하는 method
    Optional<Photo> findByFileNameAndAlbum_AlbumId(String photoName, Long albumId);

    List<Photo> findByAlbumAndFileNameContainingOrderByUploadedAtDesc(Album album, String keyword); // 사진 업로드 최신순 정렬
    List<Photo> findByAlbumAndFileNameContainingOrderByFileNameAsc(Album album, String keyword); // 파일명 A-Z 정렬
}
