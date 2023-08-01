package com.yoon.photoalbum.repository;

import com.yoon.photoalbum.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByAlbumName(String name);

    List<Album> findByAlbumNameContainingOrderByCreatedAtAsc(String keyword); // 앨범명 검색 + 생성날짜 오래된순
    List<Album> findByAlbumNameContainingOrderByCreatedAtDesc(String keyword); // 앨범명 검색 + 생성날짜 최신순

    List<Album> findByAlbumNameContainingOrderByAlbumNameAsc(String keyword); // 앨범명 검색 + 앨범명 A-Z 정렬
    List<Album> findByAlbumNameContainingOrderByAlbumNameDesc(String keyword); // 앨범명 검색 + 앨범명 Z-A 정렬

}
