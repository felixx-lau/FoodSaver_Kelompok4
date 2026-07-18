package edu.uph.m24si1.foodsaverkelompok4;

public class Constants {
    // Role
    public static final String ROLE_USER = "user";
    public static final String ROLE_PARTNER = "partner";

    // Food Status
    public static final String FOOD_AVAILABLE = "available";
    public static final String FOOD_OUT_OF_STOCK = "out_of_stock";

    // Booking Status
    public static final String BOOKING_PENDING = "pending";      // Menunggu Konfirmasi Penjual
    public static final String BOOKING_CONFIRMED = "confirmed";  // Pesanan Dikonfirmasi & Sedang Disiapkan
    public static final String BOOKING_READY = "ready";          // Pesanan Siap Diambil / Diantar
    public static final String BOOKING_COMPLETED = "completed";  // Pesanan Selesai / Sudah Diterima
    public static final String BOOKING_CANCELLED = "cancelled";  // Pesanan Dibatalkan

    // Delivery Method
    public static final String METHOD_PICKUP = "pickup";
    public static final String METHOD_DELIVERY = "delivery";
}
