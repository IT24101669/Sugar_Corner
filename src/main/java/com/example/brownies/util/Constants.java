package com.example.brownies.util;

// ==========================================
// File: src/main/java/com/example/brownies/util/Constants.java
// Purpose: Defines constant values used across the application
//          especially order status strings and role names
// ==========================================

public class Constants {

    // ---- Order Status Constants ----
    public static final String STATUS_PENDING      = "PENDING";
    public static final String STATUS_CONFIRMED    = "CONFIRMED";
    public static final String STATUS_PREPARATION  = "IN_PREPARATION";
    public static final String STATUS_READY        = "READY";
    public static final String STATUS_DELIVERED    = "DELIVERED";
    public static final String STATUS_CANCELLED    = "CANCELLED";

    // ---- Role Constants ----
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    public static final String ROLE_ADMIN    = "ROLE_ADMIN";

    // ---- Order Type Constants ----
    public static final String TYPE_DELIVERY = "DELIVERY";
    public static final String TYPE_PICKUP   = "PICKUP";

    // Private constructor to prevent instantiation
    private Constants() {}
}
