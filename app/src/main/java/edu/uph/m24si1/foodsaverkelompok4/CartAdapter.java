package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView tvFoodName, tvMethodAndTime, tvPrice, tvQuantity;
        TextView btnRemove, btnMinus, btnPlus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvMethodAndTime = itemView.findViewById(R.id.tvMethodAndTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }

        public void bind(CartItem item, int position) {
            tvFoodName.setText(item.getFoodName());
            
            String method = item.getDeliveryMethod().equalsIgnoreCase(Constants.METHOD_PICKUP) ? "Pickup" : "Delivery";
            tvMethodAndTime.setText(method + " · " + item.getTimeSlot());
            
            tvPrice.setText("Rp" + (int) item.getSubtotal());
            tvQuantity.setText(String.valueOf(item.getQuantity()));

            if (item.getPhotoUrl() != null && !item.getPhotoUrl().isEmpty()) {
                Glide.with(context).load(item.getPhotoUrl()).into(imgFood);
            }

            btnRemove.setOnClickListener(v -> {
                CartManager.getInstance().removeItem(position);
                notifyDataSetChanged();
                listener.onCartChanged();
            });

            btnMinus.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    CartManager.getInstance().updateQuantity(position, item.getQuantity() - 1);
                    notifyDataSetChanged();
                    listener.onCartChanged();
                }
            });

            btnPlus.setOnClickListener(v -> {
                CartManager.getInstance().updateQuantity(position, item.getQuantity() + 1);
                notifyDataSetChanged();
                listener.onCartChanged();
            });
        }
    }
}
