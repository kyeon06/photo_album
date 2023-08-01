package com.yoon.photoalbum.service;

import com.yoon.photoalbum.Constants;
import com.yoon.photoalbum.domain.Album;
import com.yoon.photoalbum.domain.Photo;
import com.yoon.photoalbum.dto.AlbumDto;
import com.yoon.photoalbum.mapper.AlbumMapper;
import com.yoon.photoalbum.repository.AlbumRepository;
import com.yoon.photoalbum.repository.PhotoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumServiceTest {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    AlbumService albumService;

    @Autowired
    PhotoRepository photoRepository;

    @Test
    void 앨범아이디로_앨범_정보_조회() {

        // 앨범 " 테스트 " 생성
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        AlbumDto resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
        assertEquals("테스트", resAlbum.getAlbumName());
    }

    @Test
    void 앨범명으로_앨범_정보_조회(){

        // 앨범 " 테스트1 " 생성
        Album album = new Album();
        album.setAlbumName("테스트1");
        Album savedAlbum = albumRepository.save(album);

        AlbumDto resAlbum = albumService.getAlbumByName(savedAlbum.getAlbumName());
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

    @Test
    void 앨범_사진개수_조회() {
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        // 사진 생성 -> setAlbum을 통해 앨범 지정 -> repository에 사진 저장
        Photo photo1 = new Photo();
        photo1.setFileName("사진1");
        photo1.setAlbum(savedAlbum);
        photoRepository.save(photo1);

        assertThat(photoRepository.countByAlbum_AlbumId(savedAlbum.getAlbumId())).isEqualTo(1);
    }

    @Test
    void 앨범_생성_테스트() throws IOException {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("생성 테스트");

        AlbumDto savedAlbum = albumService.createAlbum(albumDto);

        assertThat(savedAlbum.getAlbumName()).isEqualTo("생성 테스트");

        // 테스트로 인해 생성된 폴더 삭제하기
        File folder1 = new File(Constants.PATH_PREFIX + "/photos/original/" + savedAlbum.getAlbumId());
        File folder2 = new File(Constants.PATH_PREFIX + "/photos/thumb/" + savedAlbum.getAlbumId());
        folder1.delete();
        folder2.delete();
    }
}