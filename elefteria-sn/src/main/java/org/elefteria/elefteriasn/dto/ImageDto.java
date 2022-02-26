package org.elefteria.elefteriasn.dto;

import lombok.Data;
import org.elefteria.elefteriasn.entity.Image;

import java.util.Base64;

@Data
public class ImageDto {
    private String fileName;

    public ImageDto(){}

    public ImageDto(Image image){
        this.fileName = image.getFileName();
    }

    public ImageDto(String fileName) {
        this.fileName = fileName;
    }

    public static ImageDto formatToImageDto(Image image){
        return  new ImageDto(image);
    }
}
