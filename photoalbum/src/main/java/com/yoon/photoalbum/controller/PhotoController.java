package com.yoon.photoalbum.controller;

import com.yoon.photoalbum.dto.DeleteRequestBody;
import com.yoon.photoalbum.dto.MoveRequestBody;
import com.yoon.photoalbum.dto.PhotoDto;
import com.yoon.photoalbum.service.PhotoService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/albums/{albumId}/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    // 사진 상세정보 API
    @RequestMapping(value = "/{photoId}", method = RequestMethod.GET)
    public ResponseEntity<PhotoDto> getPhotoInfo(@PathVariable("photoId") final long photoId) {
        PhotoDto res = photoService.getPhoto(photoId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // 사진 업로드 API
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<List<PhotoDto>> uploadPhotos(@PathVariable("albumId") final long albumId,
                                                       @RequestParam("photos")MultipartFile[] files) throws IOException {
        List<PhotoDto> photos = new ArrayList<>();
        for (MultipartFile file : files) {
            PhotoDto photoDto = photoService.savePhoto(file, albumId);
            photos.add(photoDto);
        }
        return new ResponseEntity<>(photos, HttpStatus.OK);
    }

    // 사진 다운로드 API
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadPhotos(@RequestParam("photoIds") Long[] photoIds, HttpServletResponse response) {
        try {
            if (photoIds.length == 1) {
                File file = photoService.getImageFile(photoIds[0]);
                OutputStream outputStream = response.getOutputStream();
                IOUtils.copy(new FileInputStream(file), outputStream);
                outputStream.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 사진 목록 조회 API
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<PhotoDto>> getPhotoList(@PathVariable("albumId") final long albumId,
                                                       @RequestParam(value = "sort", required = false, defaultValue = "byDate") final String sort,
                                                       @RequestParam(value = "keyword", required = false, defaultValue = "") final String keyword) {
        List<PhotoDto> photoDtos = photoService.getPhotoList(albumId, sort, keyword);
        return new ResponseEntity<>(photoDtos, HttpStatus.OK);
    }

    // 사진 앨범 옮기기 API
    @RequestMapping(value = "/move", method = RequestMethod.PUT)
    public ResponseEntity<List<PhotoDto>> movePhotoAlbum(@RequestBody final MoveRequestBody requestBody) {
        List<PhotoDto> photoDtos = photoService.movePhoto(requestBody);
        return new ResponseEntity<>(photoDtos, HttpStatus.OK);
    }

    // 사진 삭제 API
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<List<PhotoDto>> deletePhoto(@PathVariable("albumId") final long albumId,
                                                      @RequestBody final DeleteRequestBody requestBody) {
        List<PhotoDto> photoDtos = photoService.deletePhoto(albumId, requestBody);
        return new ResponseEntity<>(photoDtos, HttpStatus.OK);
    }

}
