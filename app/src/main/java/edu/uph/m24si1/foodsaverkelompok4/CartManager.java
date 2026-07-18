package edu.uph.m24si1.foodsaverkelompok4;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage the in-memory cart items.
 */
public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(CartItem item) {
        // If item already exists in cart (same foodId), update quantity instead of adding new row
        for (CartItem existingItem : cartItems) {
            if (existingItem.getFoodId().equals(item.getFoodId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        cartItems.add(item);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void updateQuantity(int position, int newQuantity) {
        if (position >= 0 && position < cartItems.size() && newQuantity > 0) {
            cartItems.get(position).setQuantity(newQuantity);
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getItemCount() {
        return cartItems.size();
    }
}
