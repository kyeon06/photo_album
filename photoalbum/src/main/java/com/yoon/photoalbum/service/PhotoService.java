package com.yoon.photoalbum.service;

import com.yoon.photoalbum.domain.Photo;
import com.yoon.photoalbum.dto.PhotoDto;
import com.yoon.photoalbum.mapper.PhotoMapper;
import com.yoon.photoalbum.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

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
}
