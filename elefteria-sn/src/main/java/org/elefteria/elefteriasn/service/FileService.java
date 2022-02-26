package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    ResponseEntity<SuccessResponse> uploadUserAvatar(MultipartFile file);

    String uploadImageToLocalFileSystem(MultipartFile file);

    byte[] getImageByName(String imageName) throws IOException;

    void deleteImageByFileName(String fileName);
}
