package solis3d.projectvaultbackend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import solis3d.projectvaultbackend.exceptions.BadRequestException;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = this.cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "projectVault")
            );

            return uploadResult.get("secure_url").toString();
        }catch(IOException ex) {
            throw new BadRequestException("Errore nell'upload dell'immagine!");
        }
    }
}
