package com.yoon.photoalbum.repository;

import com.yoon.photoalbum.domain.Album;
import com.yoon.photoalbum.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class AlbumRepositoryTest {

    @Autowired
    AlbumRepository albumRepository;

    @Test
    void 앨범_저장소_테스트() throws InterruptedException {
        // 앨범 생성
        Album album1 = new Album();
        Album album2 = new Album();
        Album album3 = new Album();
        album1.setAlbumName("aaaa");
        album2.setAlbumName("aaab");
        album3.setAlbumName("aaac");

        // 앨범 저장
        albumRepository.save(album1);
        TimeUnit.SECONDS.sleep(5); // 생성 시간차를 두기 위해 딜레이 추가
        albumRepository.save(album2);
        TimeUnit.SECONDS.sleep(5); // 생성 시간차를 두기 위해 딜레이 추가
        albumRepository.save(album3);

        // 앨범명 검색 + 생성날짜 최신순
        List<Album> resDate = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc("aaa");
        assertEquals("aaac", resDate.get(0).getAlbumName());
        assertEquals("aaab", resDate.get(1).getAlbumName());
        assertEquals("aaaa", resDate.get(2).getAlbumName());
        assertEquals(3, resDate.size());

        // 앨범명 검색 + 앨범명 A-Z순
        List<Album> resName = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc("aaa");
        assertEquals("aaaa", resName.get(0).getAlbumName());
        assertEquals("aaab", resName.get(1).getAlbumName());
        assertEquals("aaac", resName.get(2).getAlbumName());
        assertEquals(3, resName.size());

    }
}
