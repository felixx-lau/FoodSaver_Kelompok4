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

public class PartnerFoodAdapter extends RecyclerView.Adapter<PartnerFoodAdapter.ViewHolder> {

    private Context context;
    private List<Food> foodList;
    private OnFoodActionListener listener;

    public interface OnFoodActionListener {
        void onEdit(Food food);
        void onDelete(Food food);
    }

    public PartnerFoodAdapter(Context context, List<Food> foodList, OnFoodActionListener listener) {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_partner_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bind(food);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView tvFoodName, tvPrice, tvStock, tvFoodStatus, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvFoodStatus = itemView.findViewById(R.id.tvFoodStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Food food) {
            tvFoodName.setText(food.getName());
            tvPrice.setText("Rp" + (int) food.getDiscountPrice());
            tvStock.setText("Sisa " + food.getQuantity() + " porsi");

            // Load foto
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

            if (Constants.FOOD_AVAILABLE.equals(food.getStatus())) {
                tvFoodStatus.setText("Tersedia");
                tvFoodStatus.setTextColor(0xFF2E7D32);
            } else {
                tvFoodStatus.setText("Habis");
                tvFoodStatus.setTextColor(0xFFC62828);
            }

            btnEdit.setOnClickListener(v -> listener.onEdit(food));
            btnDelete.setOnClickListener(v -> listener.onDelete(food));
        }
    }
}