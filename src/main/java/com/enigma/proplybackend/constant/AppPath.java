package com.enigma.proplybackend.constant;

public class AppPath {
    public static final String API = "/api/v1";
    public static final String AUTH = API + "/auth";
    public static final String REGISTER_ADMIN = API + AUTH + "/register/admin";
    public static final String REGISTER_EMPLOYEE = API + AUTH + "/register/employee";
    public static final String LOGIN = API + AUTH + "/login";
    public static final String DIVISIONS = API + "/division";
    public static final String USERS = API + "/users";
    public static final String ACTIVE_STATUS = "/active";
    public static final String GET_BY_ID = "/{id}";
    public static final String DELETE_BY_ID = "/delete/{id}";
}
