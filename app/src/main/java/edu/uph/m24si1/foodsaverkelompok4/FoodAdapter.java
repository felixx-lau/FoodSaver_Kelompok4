package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<Food> foodList;
    private OnFoodClickListener listener;

    public interface OnFoodClickListener {
        void onFoodClick(Food food);
    }

    public FoodAdapter(Context context, List<Food> foodList, OnFoodClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    public void updateList(List<Food> newList) {
        this.foodList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bind(food);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView tvFoodName, tvPartnerName, tvDiscountPrice, tvOriginalPrice, tvDiscount;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvPartnerName = itemView.findViewById(R.id.tvPartnerName);
            tvDiscountPrice = itemView.findViewById(R.id.tvDiscountPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
        }

        public void bind(Food food) {
            tvFoodName.setText(food.getName());
            String partnerDisplay = food.getPartnerName() != null ? food.getPartnerName() : food.getPartnerAddress();
            tvPartnerName.setText(partnerDisplay);
            tvDiscountPrice.setText("Rp" + (int) food.getDiscountPrice());
            tvOriginalPrice.setText("Rp" + (int) food.getOriginalPrice());
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            // Hitung persentase diskon
            if (food.getOriginalPrice() > 0) {
                int persen = (int) (((food.getOriginalPrice() - food.getDiscountPrice())
                        / food.getOriginalPrice()) * 100);
                tvDiscount.setText("Hemat " + persen + "%");
            }

            // Load foto dari URL pakai Glide
            if (food.getPhotoUrl() != null && !food.getPhotoUrl().isEmpty()) {
                Glide.with(context)
                        .load(food.getPhotoUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .centerCrop()
                        .into(imgFood);
            } else {
                imgFood.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Klik item makanan
            itemView.setOnClickListener(v -> listener.onFoodClick(food));
        }
    }
}
