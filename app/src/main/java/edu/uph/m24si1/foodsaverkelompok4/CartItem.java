package edu.uph.m24si1.foodsaverkelompok4;

/**
 * Model class for items in the shopping cart.
 */
public class CartItem {
    private String foodId;
    private String foodName;
    private String partnerId;
    private String partnerName;
    private String partnerAddress;
    private double discountPrice;
    private int quantity;
    private String deliveryMethod;
    private String timeSlot;
    private String photoUrl;

    public CartItem() {}

    public CartItem(String foodId, String foodName, String partnerId, String partnerName, 
                    String partnerAddress, double discountPrice, int quantity, 
                    String deliveryMethod, String timeSlot, String photoUrl) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.partnerAddress = partnerAddress;
        this.discountPrice = discountPrice;
        this.quantity = quantity;
        this.deliveryMethod = deliveryMethod;
        this.timeSlot = timeSlot;
        this.photoUrl = photoUrl;
    }

    // Getters and Setters
    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }

    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }

    public String getPartnerAddress() { return partnerAddress; }
    public void setPartnerAddress(String partnerAddress) { this.partnerAddress = partnerAddress; }

    public double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(double discountPrice) { this.discountPrice = discountPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public double getSubtotal() {
        return discountPrice * quantity;
    }
}
