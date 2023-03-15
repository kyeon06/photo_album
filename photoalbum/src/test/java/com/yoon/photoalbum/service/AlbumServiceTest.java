package com.yoon.photoalbum.service;

import com.yoon.photoalbum.domain.Album;
import com.yoon.photoalbum.repository.AlbumRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumServiceTest {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    AlbumService albumService;

    @Test
    void 앨범아이디로_앨범_정보_조회() {

        // 앨범 " 테스트 " 생성
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        Album resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
        assertEquals("테스트", resAlbum.getAlbumName());
    }

    @Test
    void 앨범명으로_앨범_정보_조회(){

        // 앨범 " 테스트1 " 생성
        Album album = new Album();
        album.setAlbumName("테스트1");
        Album savedAlbum = albumRepository.save(album);

        Album resAlbum = albumService.getAlbumByName(savedAlbum.getAlbumName());
        assertEquals("테스트1", resAlbum.getAlbumName());
    }

    @Test
    void 앨범아이디_조회_예외처리() {
        // 앨범 " 테스트 " 생성
        Album album = new Album();
        album.setAlbumName("테스트");
        album.setAlbumId(1234L);
        Album savedAlbum = albumRepository.save(album);

        // Id 조회가 실패할 경우 예외 던짐
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            albumService.getAlbum(12345L);
        });
    }
}