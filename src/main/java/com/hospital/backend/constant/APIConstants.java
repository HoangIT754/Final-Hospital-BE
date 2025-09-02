package com.hospital.backend.constant;

public class APIConstants {

    private APIConstants() {
    }

    public static final String API_VERSION = "api/v1/app";

    // Authentication api
    public static final String API_AUTH = API_VERSION + "/auth";
    public static final String API_LOGIN = API_AUTH + "/login";
    public static final String API_LOGOUT = API_AUTH + "/logout";
    public static final String API_SIGNUP = API_AUTH + "/signup";

    // User api
    public static final String API_USER = API_VERSION + "/user";
    public static final String API_CREATE_USER = API_USER + "/register";

    // Doctor api
    public static final String API_DOCTOR = API_USER + "/doctor";
    public static final String API_CREATE_DOCTOR = API_DOCTOR + "/create-doctor";
    public static final String API_GET_ALL_DOCTOR = API_DOCTOR + "/get-all-doctor";

    // Patient api
    public static final String API_PATIENT = API_VERSION + "/patient";
    public static final String API_CREATE_PATIENT = API_PATIENT + "/create-patient";
    public static final String API_GET_ALL_PATIENT = API_PATIENT + "/get-all-patient";
    public static final String API_GET_PATIENT_COUNT_BY_STATUS = API_PATIENT + "/count-by-status";
    public static final String API_SEARCH_PATIENT = API_PATIENT + "/search";

    // Appointment api
    public static final String API_APPOINTMENT = API_VERSION + "/appointments";
    public static final String API_APPOINTMENT_CREATE = API_APPOINTMENT + "/create-appointment";
    public static final String API_APPOINTMENT_GET_ALL = API_APPOINTMENT + "/get-all-appointments";
    public static final String API_APPOINTMENT_GET_DETAILS = API_APPOINTMENT + "/get-appointment-details";
    public static final String API_APPOINTMENT_UPDATE = API_APPOINTMENT + "/update-appointment";
    public static final String API_APPOINTMENT_DELETE = API_APPOINTMENT + "/delete-appointment";

    // Department api
    public static final String API_DEPARTMENT = API_VERSION + "/department";
    public static final String API_CREATE_DEPARTMENT = API_DEPARTMENT + "/create";
    public static final String API_UPDATE_DEPARTMENT = API_DEPARTMENT + "/update";
    public static final String API_DELETE_DEPARTMENT = API_DEPARTMENT + "/delete";
    public static final String API_GET_DEPARTMENT_BY_ID = API_DEPARTMENT + "/get";
    public static final String API_GET_ALL_DEPARTMENTS = API_DEPARTMENT + "/get-all";

    // Room api
    public static final String API_ROOM = API_VERSION + "/room";
    public static final String API_CREATE_ROOM = API_ROOM + "/create";
    public static final String API_UPDATE_ROOM = API_ROOM + "/update";
    public static final String API_DELETE_ROOM = API_ROOM + "/delete";
    public static final String API_GET_ROOM_BY_ID = API_ROOM + "/get";
    public static final String API_GET_ALL_ROOMS = API_ROOM + "/get-all";

    // Service api
    public static final String API_SERVICE = API_VERSION + "/service";
    public static final String API_CREATE_SERVICE = API_SERVICE + "/create";
    public static final String API_UPDATE_SERVICE = API_SERVICE + "/update";
    public static final String API_DELETE_SERVICE = API_SERVICE + "/delete";
    public static final String API_GET_SERVICE_BY_ID = API_SERVICE + "/get";
    public static final String API_GET_ALL_SERVICES = API_SERVICE + "/get-all";

    // Lab Test api
    public static final String API_LAB_TEST = API_VERSION + "/lab-test";
    public static final String API_CREATE_LAB_TEST = API_LAB_TEST + "/create";
    public static final String API_UPDATE_LAB_TEST = API_LAB_TEST + "/update";
    public static final String API_DELETE_LAB_TEST = API_LAB_TEST + "/delete";
    public static final String API_GET_LAB_TEST_BY_ID = API_LAB_TEST + "/get";
    public static final String API_GET_ALL_LAB_TESTS = API_LAB_TEST + "/get-all";

}
