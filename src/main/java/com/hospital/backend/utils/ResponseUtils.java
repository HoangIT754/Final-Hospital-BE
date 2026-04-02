package com.hospital.backend.utils;

import com.hospital.backend.constant.Constants;
import com.hospital.backend.dto.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Slf4j
public class ResponseUtils {

    /**
     * Prevent instantiation of utility class.
     */
    private ResponseUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated!");
    }

    public static <T> BaseResponse buildSuccessRes(T data) {
        try {
            return new BaseResponse(
                    200,
                    data,
                    Constants.MESSAGE_OPERATION_SUCCESSFUL,
                    Constants.SUCCESS,
                    0,
                    Constants.SUCCESS,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        } catch (Exception e) {
            return new BaseResponse(
                    500,
                    null,
                    Constants.ERROR_INTERNAL_SERVER,
                    Constants.ERROR_INTERNAL_SERVER,
                    1,
                    e.getMessage(),
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }
    public static <T> BaseResponse buildSuccessRes(T data, String description) {
        try {
            return new BaseResponse(
                    200,
                    data,
                    description,
                    Constants.SUCCESS,
                    0,
                    Constants.SUCCESS,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        } catch (Exception e) {
            return new BaseResponse(
                    500,
                    null,
                    Constants.ERROR_INTERNAL_SERVER,
                    Constants.ERROR_INTERNAL_SERVER,
                    1,
                    e.getMessage(),
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }

    public static <T> BaseResponse buildInternalError(String description) {
        try {
            return new BaseResponse(
                    500,
                    null,
                    description,
                    Constants.ERROR_INTERNAL_SERVER,
                    1,
                    Constants.ERROR_INTERNAL_SERVER,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        } catch (Exception e) {
            return new BaseResponse(
                    500,
                    null,
                    Constants.ERROR_INTERNAL_SERVER,
                    Constants.ERROR_INTERNAL_SERVER,
                    1,
                    e.getMessage(),
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }

    public static <T> BaseResponse buildNotFoundRes(T data, String description) {
        try {
            return new BaseResponse(
                    HttpStatus.NOT_FOUND.value(),
                    data,
                    description,
                    Constants.ERROR_NOT_FOUND,
                    0,
                    Constants.ERROR_NOT_FOUND,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        } catch (Exception e) {
            return new BaseResponse(
                    500,
                    null,
                    Constants.ERROR_INTERNAL_SERVER,
                    Constants.ERROR_INTERNAL_SERVER,
                    1,
                    e.getMessage(),
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }
}