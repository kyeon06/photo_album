package com.yoon.photoalbum.service;

import com.yoon.photoalbum.Constants;
import com.yoon.photoalbum.domain.Album;
import com.yoon.photoalbum.domain.Photo;
import com.yoon.photoalbum.dto.AlbumDto;
import com.yoon.photoalbum.mapper.AlbumMapper;
import com.yoon.photoalbum.repository.AlbumRepository;
import com.yoon.photoalbum.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    // 앨범아이디로 앨범 조회
    public AlbumDto getAlbum(Long albumId){
        Optional<Album> res = albumRepository.findById(albumId);
        if (res.isPresent()){
            AlbumDto albumDto = AlbumMapper.convertToDto(res.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumId));
            return albumDto;
        }
        else{
            throw new EntityNotFoundException(String.format("앨범 아이디 %d로 조회되지 않았습니다.", albumId));
        }
    }

    // 앨범명으로 앨범 조회
    public AlbumDto getAlbumByName(String albumName){
        Optional<Album> res = albumRepository.findByAlbumName((albumName));
        if (res.isPresent()){
            AlbumDto albumDto = AlbumMapper.convertToDto(res.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumDto.getAlbumId()));
            return albumDto;
        } else {
            throw new EntityNotFoundException(String.format("앨범명 %s로 조회되지 않았습니다.", albumName));
        }
    }

    // 앨범 생성
    public  AlbumDto createAlbum(AlbumDto albumDto) throws IOException {
        // DTO -> Domain 변환
        Album album = AlbumMapper.convertToModel(albumDto);
        // DB 저장
        this.albumRepository.save(album);
        // 앨범 ID를 사용해서 폴더 생성
        this.createAlbumDirectories(album);

        // DTO 변환해서 반환
        return AlbumMapper.convertToDto(album);
    }

    // 앨범 ID로 폴더 생성하는 method
    private void createAlbumDirectories(Album album) throws IOException {
        Files.createDirectories(Paths.get(Constants.PATH_PREFIX + "/photos/original/" + album.getAlbumId()));
        Files.createDirectories(Paths.get(Constants.PATH_PREFIX + "/photos/thumb/" + album.getAlbumId()));
    }

    // 앨범 목록 조회
    public List<AlbumDto> getAlbumList(String sort, String keyword, String orderBy) {
        List<Album> albums;

        if (Objects.equals(sort, "byName")) {
            if (Objects.equals(orderBy, "asc")) {
                albums = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc(keyword);
            } else if (Objects.equals(orderBy, "desc")){
                albums = albumRepository.findByAlbumNameContainingOrderByAlbumNameDesc(keyword);
            } else {
                throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");
            }
        } else if (Objects.equals(sort, "byDate")) {
            if (Objects.equals(orderBy, "asc")) {
                albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtAsc(keyword);
            } else if (Objects.equals(orderBy, "desc")){
                albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc(keyword);
            } else {
                throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");
            }
        } else {
            throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");
        }

        // Model -> DTO 변환
        List<AlbumDto> albumDtos = AlbumMapper.convertToDtoList(albums);

        // 앨범 별로 최신 4장의 thumbUrls 저장
        for (AlbumDto albumDto : albumDtos) {
            List<Photo> top4 = photoRepository.findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(albumDto.getAlbumId());
            albumDto.setThumbUrls(top4.stream().map(Photo::getThumbUrl).map(c -> Constants.PATH_PREFIX + c).collect(Collectors.toList()));
        }

        return albumDtos;
    }

    // 앨범명 변경
    public AlbumDto changeAlbumName(Long albumId, AlbumDto albumDto) {
        // albumId로 기존 앨범 정보 불러오기
        Optional<Album> album = this.albumRepository.findById(albumId);
        // 앨범 정보가 없는 경우 예외처리
        if (album.isEmpty()) {
            throw new NoSuchElementException(String.format("Album ID '%d'가 존재하지 않습니다.", albumId));
        }

        Album updateAlbum = album.get();
        updateAlbum.setAlbumName(albumDto.getAlbumName()); // 요청 받은 앨범 명으로 앨범명 바꾸기
        Album savedAlbum = this.albumRepository.save(updateAlbum); // 변경 후 저장

        return AlbumMapper.convertToDto(savedAlbum);
    }
}
