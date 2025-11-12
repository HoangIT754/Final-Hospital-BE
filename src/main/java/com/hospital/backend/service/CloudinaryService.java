package com.hospital.backend.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    // Upload image to folder and return secure_url
    public String uploadImage(MultipartFile file, String folder, String publicIdHint) throws IOException {
        Map<?, ?> res = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", publicIdHint,
                        "overwrite", true,
                        "resource_type", "image"
                )
        );
        return (String) res.get("secure_url");
    }

    // delete an image with public id
    public void deleteByPublicId(String publicId) throws IOException {
        if (publicId == null || publicId.isBlank()) return;
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
    }

    // extract public id
    public String extractPublicIdFromUrl(String secureUrl) {
        if (secureUrl == null || secureUrl.isBlank()) return null;

        String decoded = URLDecoder.decode(secureUrl, StandardCharsets.UTF_8); // https://res.cloudinary.com/<cloud_name>/image/upload/v1731253456/avatars/users/123e.../avatar.jpg
        int idx = decoded.indexOf("/upload/");
        if (idx < 0) return null;
        String tail = decoded.substring(idx + "/upload/".length());

        if (tail.startsWith("v")) {
            int slash = tail.indexOf('/');
            if (slash > 0) tail = tail.substring(slash + 1);
        }

        // remove .jpg, .png, etc
        int dot = tail.lastIndexOf('.');
        if (dot > 0) tail = tail.substring(0, dot);
        return tail;
    }
}
