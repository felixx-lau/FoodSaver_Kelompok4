package edu.uph.m24si1.foodsaverkelompok4;

public class Booking {
    private String bookingId;
    private String userId;
    private String foodId;
    private String partnerId;
    private String partnerName;
    private String foodName;
    private String userName;
    private int quantity;
    private String status;
    private String deliveryMethod;
    private String deliveryAddress;
    private String scheduledTimeSlot;
    private long timestamp;

    public Booking() {}

    public Booking(String bookingId, String userId, String foodId, String partnerId, 
                   String foodName, String userName, int quantity, String status, 
                   String deliveryMethod, String deliveryAddress, String scheduledTimeSlot, 
                   long timestamp) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.foodId = foodId;
        this.partnerId = partnerId;
        this.foodName = foodName;
        this.userName = userName;
        this.quantity = quantity;
        this.status = status;
        this.deliveryMethod = deliveryMethod;
        this.deliveryAddress = deliveryAddress;
        this.scheduledTimeSlot = scheduledTimeSlot;
        this.timestamp = timestamp;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }

    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getScheduledTimeSlot() { return scheduledTimeSlot; }
    public void setScheduledTimeSlot(String scheduledTimeSlot) { this.scheduledTimeSlot = scheduledTimeSlot; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
