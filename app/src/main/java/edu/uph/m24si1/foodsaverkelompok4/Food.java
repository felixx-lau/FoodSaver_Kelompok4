package edu.uph.m24si1.foodsaverkelompok4;

public class Food {
    private String foodId;
    private String partnerId;
    private String name;
    private String description;
    private double originalPrice;
    private double discountPrice;
    private String photoUrl;
    private int quantity;
    private String status;
    private String partnerName;
    private String partnerAddress;
    private long createdAt;


    public Food() {}

    public Food(String foodId, String partnerId, String name, String description,
                double originalPrice, double discountPrice, String photoUrl,
                int quantity, String status, String partnerName, String partnerAddress, long createdAt) {
        this.foodId = foodId;
        this.partnerId = partnerId;
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.photoUrl = photoUrl;
        this.quantity = quantity;
        this.status = status;
        this.partnerName = partnerName;
        this.partnerAddress = partnerAddress;
        this.createdAt = createdAt;
    }

    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(double discountPrice) { this.discountPrice = discountPrice; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }

    public String getPartnerAddress() { return partnerAddress; }
    public void setPartnerAddress(String partnerAddress) { this.partnerAddress = partnerAddress; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
