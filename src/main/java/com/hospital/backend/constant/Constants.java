package com.hospital.backend.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;


/**
 * A utility class containing constants for use in the application.
 * This class includes constants for security schemes, token headers,
 * date formats, and predefined user roles.
 */
public final class Constants {
    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    public static final String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_TYPE = "Bearer";

    public static final int EMAIL_VERIFICATION_TOKEN_LENGTH = 64;

    public static final int PASSWORD_RESET_TOKEN_LENGTH = 32;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String TIME_ZONE_ID = "Asia/Ho_Chi_Minh";

    public static final String PUBLIC_SCHEMA = "public";

    public static final String WS4_SCHEMA = "ws4";

    public static final String TRANSACTION_SOURCE = "NWF";

    public static final String DNA_SUCCESS_CODE = "00000000";
    public static final String SYNC_SUCCESS_CODE = "0";
    public static final String DNA_SUCCESS_CODE_1 = "00000001";
    public static final String HTTP_SUCCESS_CODE = "200";

    public static final String LOG_SUCCESS = "SUCCESS";
    public static final String SUCCESS = "SUCCESS";
    public static final String OPERATION_SUCCESS = "OPERATION_SUCCESS";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String LOG_FAIL = "FAIL";
    public static final String LOG_WAITING = "WAITING";
    public static final String LOG_BYPASS = "BYPASS";
    public static final String DEFAULT = "DEFAULT";

    public static final String API_SDF_FORMAT = "EEE MMM dd yyyy DDD HH:mm:ss z";
    public static final String FAILED = "Failed";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String ERROR_UNEXPECTED = "Unexpected error occurred";
    public static final String ERROR_INTERNAL_SERVER = "Internal Server Error";
    public static final String ERROR_BAD_REQUEST = "Bad Request";
    public static final String ERROR_NOT_FOUND = "Not Found";
    public static final String ERROR_GENERIC = "Error";
    public static final String ERROR_NO_DATA = "No data";
    public static final String MESSAGE_OPERATION_SUCCESSFUL = "Operation successful";
    public static final String BOOLEAN_FALSE = "false";

    public static final String BEARER = "Bearer ";
    public static final String KQLC = "KQLC";

    private Constants() {
    }

    /**
     * Enumeration representing user roles within the system.
     * It provides predefined roles such as ADMIN and USER.
     */
    @Getter
    @AllArgsConstructor
    public enum RoleEnum {
        ADMIN("ADMIN"),
        USER("USER");

        private final String value;

        public static RoleEnum get(final String name) {
            return Stream.of(RoleEnum.values())
                    .filter(p -> p.name().equals(name.toUpperCase()) || p.getValue().equals(name.toUpperCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid role name: %s", name)));
        }
    }
}
