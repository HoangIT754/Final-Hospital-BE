package com.hospital.backend.service;

import com.hospital.backend.dto.request.medicine.MedicineRequest;
import com.hospital.backend.dto.request.medicine.MedicineSearchRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.Medicine;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.repository.MedicineRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final MedicineRepository medicineRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Create Medicine
     */
    @Transactional
    public BaseResponse createMedicine(MedicineRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            // Validate cơ bản
            if (request.getName() == null || request.getName().isBlank()) {
                throw new BadRequestException("Medicine name is required");
            }
            if (request.getPrice() == null) {
                throw new BadRequestException("Medicine price is required");
            }
            if (request.getCurrency() == null || request.getCurrency().isBlank()) {
                throw new BadRequestException("Medicine currency is required");
            }

            // Check trùng code (nếu có truyền)
            if (request.getCode() != null && !request.getCode().isBlank()) {
                Optional<Medicine> existed =
                        medicineRepository.findByCodeIgnoreCase(request.getCode());
                if (existed.isPresent()) {
                    throw new BadRequestException("Medicine code already exists");
                }
            }

            Medicine medicine = new Medicine();
            medicine.setCode(request.getCode());
            medicine.setName(request.getName());
            medicine.setDescription(request.getDescription());
            medicine.setForm(request.getForm());
            medicine.setStrength(request.getStrength());
            medicine.setUnit(request.getUnit());
            medicine.setStock(request.getStock());
            medicine.setPrice(request.getPrice());
            medicine.setCurrency(request.getCurrency());
            medicine.setManufacturer(request.getManufacturer());
            medicine.setIsActive(
                    request.getIsActive() != null ? request.getIsActive() : true
            );

            // ===== Upload ảnh nếu có =====
            if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
                try {
                    String folder = "hospital/medicines"; // tuỳ bạn đặt
                    String publicIdHint = (request.getCode() != null && !request.getCode().isBlank())
                            ? request.getCode()
                            : UUID.randomUUID().toString();

                    String secureUrl = cloudinaryService.uploadImage(request.getImageFile(), folder, publicIdHint);
                    String publicId = cloudinaryService.extractPublicIdFromUrl(secureUrl);

                    medicine.setImageUrl(secureUrl);
                    medicine.setImagePublicId(publicId);
                } catch (IOException ex) {
                    log.error("Error while uploading medicine image to Cloudinary", ex);
                    throw new BadRequestException("Failed to upload medicine image");
                }
            }

            Medicine saved = medicineRepository.save(medicine);

            log.info("End create Medicine in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(saved, "Created Medicine Successfully");

        } catch (BadRequestException e) {
            log.error("Validation error while creating medicine: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating medicine", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Get All Medicines
     */
    public BaseResponse getAllMedicines() {
        log.info("Started fetching all medicines");
        long beginTime = System.currentTimeMillis();

        try {
            List<Medicine> medicines = medicineRepository.findAll();

            log.info("End fetching medicines in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(medicines, medicines.size()),
                    "Fetched Medicines Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching medicines", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Search Medicines with filters
     */
    public BaseResponse searchMedicine(MedicineSearchRequest request) {
        try {
            String code = request.getCode();
            String name = request.getName();
            String manufacturer = request.getManufacturer();

            if (code != null && !code.isBlank()) {
                code = "%" + code.toLowerCase() + "%";
            } else {
                code = null;
            }

            if (name != null && !name.isBlank()) {
                name = "%" + name.toLowerCase() + "%";
            } else {
                name = null;
            }

            if (manufacturer != null && !manufacturer.isBlank()) {
                manufacturer = "%" + manufacturer.toLowerCase() + "%";
            } else {
                manufacturer = null;
            }

            Boolean isActive = request.getIsActive();

            List<Medicine> medicines = medicineRepository.searchMedicines(
                    code,
                    name,
                    manufacturer,
                    isActive
            );

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(medicines, medicines.size()),
                    "Fetched Medicines Successfully"
            );
        } catch (Exception e) {
            log.error("Error while searching medicines", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }
}
