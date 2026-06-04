package solis3d.projectvaultbackend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import solis3d.projectvaultbackend.exceptions.BadRequestException;
import solis3d.projectvaultbackend.payloads.CloudinaryUploadRespDTO;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public CloudinaryUploadRespDTO uploadImage(MultipartFile file) {
        try {
            Map uploadResult = this.cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "projectVault")
            );

            return new CloudinaryUploadRespDTO(
                    uploadResult.get("secure_url").toString(),
                    uploadResult.get("public_id").toString()
            );
        }catch(IOException ex) {
            throw new BadRequestException("Errore nell'upload dell'immagine!");
        }
    }

    public void deleteImage(String publicId) {
        if(publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            this.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException ex) {
            throw new BadRequestException("Errore nella cancellazione dell'immagine!");
        }
    }

    public void deleteImageByUrl(String imageUrl) {
        String publicId = this.extractPublicIdFromUrl(imageUrl);
        this.deleteImage(publicId);
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        if(imageUrl == null || !imageUrl.contains("/image/upload/")) {
            return null;
        }

        String publicIdWithExtension = imageUrl.substring(imageUrl.indexOf("/image/upload/") + "/image/upload/".length());

        publicIdWithExtension = publicIdWithExtension.replaceFirst("^v\\d+/", "");

        int dotIndex = publicIdWithExtension.lastIndexOf(".");

        if(dotIndex == -1) {
            return publicIdWithExtension;
        }

        return publicIdWithExtension.substring(0, dotIndex);
    }
}
