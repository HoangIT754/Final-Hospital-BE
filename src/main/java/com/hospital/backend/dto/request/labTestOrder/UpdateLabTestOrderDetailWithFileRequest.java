package com.hospital.backend.dto.request.labTestOrder;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateLabTestOrderDetailWithFileRequest {
    UUID detailId;
    String result;
    String status;
    List<MultipartFile> files;
}
