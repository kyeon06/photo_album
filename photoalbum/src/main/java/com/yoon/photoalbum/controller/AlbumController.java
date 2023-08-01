package com.yoon.photoalbum.controller;

import com.yoon.photoalbum.dto.AlbumDto;
import com.yoon.photoalbum.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    AlbumService albumService;

    @RequestMapping(value = "/{albumId}", method = RequestMethod.GET)
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable("albumId") final long albumId) {
        // albumId에 해당하는 album 정보 가져오기
        AlbumDto album = albumService.getAlbum(albumId);
        // json 형식으로 200 코드와 함께 반환한다.
        return new ResponseEntity<>(album, HttpStatus.OK);
    }
}
