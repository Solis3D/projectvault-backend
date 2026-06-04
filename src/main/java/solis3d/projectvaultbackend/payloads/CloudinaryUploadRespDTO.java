package solis3d.projectvaultbackend.payloads;

public record CloudinaryUploadRespDTO(
        String imageUrl,
        String publicId
) {
}
