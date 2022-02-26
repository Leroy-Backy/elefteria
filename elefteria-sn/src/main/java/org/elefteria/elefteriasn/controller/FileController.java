package org.elefteria.elefteriasn.controller;

import org.elefteria.elefteriasn.response.SuccessResponse;
import org.elefteria.elefteriasn.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileController {

    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @PostMapping("/images/avatar")
    public ResponseEntity<SuccessResponse> uploadUserAvatar(@RequestParam("file")MultipartFile file){

        return fileService.uploadUserAvatar(file);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/images/{imageName:.+}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody byte[] getImageByName(@PathVariable(name = "imageName") String imageName) throws IOException {
        return this.fileService.getImageByName(imageName);
    }
}
