package com.yoon.photoalbum.service;

import com.yoon.photoalbum.domain.Album;
import com.yoon.photoalbum.dto.AlbumDto;
import com.yoon.photoalbum.mapper.AlbumMapper;
import com.yoon.photoalbum.repository.AlbumRepository;
import com.yoon.photoalbum.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

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

    public AlbumDto getAlbumByName(String albumName){
        Optional<Album> res = albumRepository.findByAlbumName((albumName));
        if (res.isPresent()){
            AlbumDto albumDto = AlbumMapper.convertToDto(res.get());
            return albumDto;
        } else {
            throw new EntityNotFoundException(String.format("앨범명 %s로 조회되지 않았습니다.", albumName));
        }
    }
}
