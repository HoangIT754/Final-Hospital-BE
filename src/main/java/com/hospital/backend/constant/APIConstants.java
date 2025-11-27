package com.hospital.backend.constant;

public class APIConstants {

    private APIConstants() {
    }

    public static final String API_VERSION = "api/v1/app";

    // Authentication api
    public static final String API_AUTH = API_VERSION + "/auth";
    public static final String API_LOGIN = API_AUTH + "/login";
    public static final String API_LOGIN_GOOGLE = API_LOGIN + "/login-google";
    public static final String API_LOGOUT = API_AUTH + "/logout";
    public static final String API_SIGNUP = API_AUTH + "/signup";
    public static final String API_ASSIGN_ROLE = API_AUTH + "/assign-role";

    // User api
    public static final String API_USER = API_VERSION + "/user";
    public static final String API_CREATE_USER = API_USER + "/register";
    public static final String API_COUNT_ALL_ROLES = API_USER + "/count-all-roles";
    public static final String API_GET_ALL_USER = API_USER + "/get-all";
    public static final String API_GET_USER_BY_ID = API_USER + "/get-by-id";
    public static final String API_UPDATE_USER = API_USER + "/update";
    public static final String API_GET_USER_BY_USERNAME = API_USER + "/get-by-username";
    public static final String API_GET_USER_PROFILE_BY_ROLE = API_USER + "/get-by-role";

    // Staff api
    public static final String API_STAFF = API_USER + "/staff";
    public static final String API_CREATE_STAFF = API_STAFF + "/create-staff";
    public static final String API_GET_ALL_STAFF = API_STAFF + "/get-all-staff";
    public static final String API_GET_ALL_DOCTOR = API_STAFF + "/get-all-doctor";
    public static final String API_GET_STAFF_BY_USERNAME = API_STAFF + "/get-staff-by-username";

    // Patient api
    public static final String API_PATIENT = API_VERSION + "/patient";
    public static final String API_CREATE_PATIENT = API_PATIENT + "/create-patient";
    public static final String API_GET_ALL_PATIENT = API_PATIENT + "/get-all-patient";
    public static final String API_GET_PATIENT_COUNT_BY_STATUS = API_PATIENT + "/count-by-status";
    public static final String API_SEARCH_PATIENT = API_PATIENT + "/search";
    public static final String API_GET_PATIENT_STATUS = API_PATIENT + "/get-patient-status";
    public static final String API_GET_PATIENT_BY_DOCTOR = API_PATIENT + "/get-patient-by-doctor";

    // Appointment api
    public static final String API_APPOINTMENT = API_VERSION + "/appointments";
    public static final String API_APPOINTMENT_CREATE = API_APPOINTMENT + "/create-appointment";
    public static final String API_APPOINTMENT_GET_ALL = API_APPOINTMENT + "/get-all";
    public static final String API_APPOINTMENT_SEARCH = API_APPOINTMENT + "/search";
    public static final String API_GET_APPOINTMENT_BY_ID = API_APPOINTMENT + "/get-by-id";
    public static final String API_APPOINTMENT_GET_DETAILS = API_APPOINTMENT + "/get-appointment-details";
    public static final String API_APPOINTMENT_UPDATE = API_APPOINTMENT + "/update-appointment";
    public static final String API_APPOINTMENT_DELETE = API_APPOINTMENT + "/delete-appointment";

    // Room api
    public static final String API_ROOM = API_VERSION + "/room";
    public static final String API_CREATE_ROOM = API_ROOM + "/create";
    public static final String API_UPDATE_ROOM = API_ROOM + "/update";
    public static final String API_DELETE_ROOM = API_ROOM + "/delete";
    public static final String API_GET_ROOM_BY_ID = API_ROOM + "/get";
    public static final String API_GET_ALL_ROOMS = API_ROOM + "/get-all";
    public static final String API_SEARCH_ROOMS = API_ROOM + "/search-room";

    // Floor api
    public static final String API_FLOOR = API_VERSION + "/floor";
    public static final String API_GET_FLOORS_BY_AREA = API_FLOOR + "/get-floors-by-area";

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

    // Lab Test Order api
    public static final String API_LAB_TEST_ORDER = API_VERSION + "/lab-test-order";
    public static final String API_CREATE_LAB_TEST_ORDER = API_LAB_TEST_ORDER + "/create";
    public static final String API_GET_ALL_LAB_TESTS_ORDER = API_LAB_TEST_ORDER + "/get-all";
    public static final String API_GET_LAB_TESTS_ORDER_BY_ID = API_LAB_TEST_ORDER + "/get-by-id";
    public static final String API_UPDATE_LAB_TEST_ORDER_DETAIL_WITH_FILE = API_LAB_TEST_ORDER + "/update-lab-test-order-detail";

    // Specialty api
    public static final String API_SPECIALTY = API_VERSION + "/specialty";
    public static final String API_CREATE_SPECIALTY = API_SPECIALTY + "/create";
    public static final String API_GET_ALL_SPECIALTITIES = API_SPECIALTY + "/get-all";

    // Role api
    public static final String API_ROLE = API_VERSION + "/role";
    public static final String API_CREATE_ROLE = API_ROLE + "/create";
    public static final String API_GET_ALL_ROLES = API_ROLE + "/get-all";

    // Work Schedule api
    public static final String API_WORK_SCHEDULE = API_VERSION + "/work-schedule";
    public static final String API_WORK_SCHEDULE_CREATE = API_WORK_SCHEDULE + "/create";

    // Staff status api
    public static final String API_STAFF_STATUS = API_VERSION + "/staff-status";
    public static final String API_CREATE_STAFF_STATUS = API_STAFF_STATUS + "/create";

    // Area api
    public static final String API_AREA = API_VERSION + "/area";
    public static final String API_GET_ALL_AREA = API_AREA + "/get-all";

    // Medical record api
    public static final String API_MEDICAL_RECORD = API_VERSION + "/medical-record";
    public static final String API_CREATE_OR_UPDATE_MEDICAL_RECORD = API_MEDICAL_RECORD + "/create-or-update";
    public static final String API_GET_MEDICAL_RECORD_BY_APPOINTMENT_ID = API_MEDICAL_RECORD + "/get-by-appointment-id";

    // Medicine api
    public static final String API_MEDICINE = API_VERSION + "/medicine";
    public static final String API_CREATE_MEDICINE = API_MEDICINE + "/create";
    public static final String API_GET_ALL_MEDICINE = API_MEDICINE + "/get-all";
    public static final String API_SEARCH_MEDICINE = API_MEDICINE + "/saerch";

}
