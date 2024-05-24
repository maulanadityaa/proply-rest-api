package com.enigma.proplybackend.constant;

public class AppPath {
    public static final String API = "/api/v1";
    public static final String AUTH = API + "/auth";
    public static final String REGISTER_ADMIN = "/register/admin";
    public static final String REGISTER_EMPLOYEE = "/register/employee";
    public static final String REGISTER_MANAGER = "/register/manager";
    public static final String LOGIN = "/login";
    public static final String DIVISIONS = API + "/divisions";
    public static final String USERS = API + "/users";
    public static final String USER_PROFILES = API + "/user-profiles";
    public static final String ITEM_CATEGORY = API + "/item-categories";
    public static final String ITEM = API + "/items";
    public static final String PROCUREMENT_CATEGORY = API + "/procurement-categories";
    public static final String PROCUREMENT = API + "/procurements";
    public static final String APPROVE_PROCUREMENT = "/approve";
    public static final String REJECT_PROCUREMENT = "/reject";
    public static final String CANCEL_PROCUREMENT = "/cancel";
    public static final String ACTIVE_STATUS = "/active";
    public static final String GET_BY_ID = "/{id}";
    public static final String GET_BY_EMAIL = "/email";
    public static final String GET_WITH_PAGE = "/page";
    public static final String GET_BY_USER_ID = "/search";
    public static final String DELETE_BY_ID = "/delete/{id}";
}
