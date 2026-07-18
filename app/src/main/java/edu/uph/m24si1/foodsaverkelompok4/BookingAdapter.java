package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    public void updateList(List<Booking> newList) {
        this.bookingList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {

        TextView tvFoodName, tvQuantityInfo, tvStatus, tvMethodAndTime, tvPrice;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantityInfo = itemView.findViewById(R.id.tvQuantityInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMethodAndTime = itemView.findViewById(R.id.tvMethodAndTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        public void bind(Booking booking) {
            tvFoodName.setText(booking.getFoodName());
            if (booking.getPartnerName() != null) {
                tvFoodName.setText(booking.getFoodName() + " (" + booking.getPartnerName() + ")");
            }
            tvQuantityInfo.setText(booking.getQuantity() + " porsi");

            // Method dan waktu
            String methodIcon = Constants.METHOD_PICKUP.equals(booking.getDeliveryMethod())
                    ? "🚶 Pickup" : "🚴 Delivery";
            tvMethodAndTime.setText(methodIcon + " · " + booking.getScheduledTimeSlot());

            // Status badge — warna berbeda per status
            setStatusBadge(booking.getStatus());
        }

        private void setStatusBadge(String status) {
            switch (status) {
                case Constants.BOOKING_PENDING:
                    tvStatus.setText("Menunggu Konfirmasi");
                    tvStatus.setTextColor(Color.parseColor("#E65100"));
                    tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
                    break;

                case Constants.BOOKING_CONFIRMED:
                    tvStatus.setText("Pesanan Sedang Disiapkan");
                    tvStatus.setTextColor(Color.parseColor("#7B1FA2"));
                    tvStatus.setBackgroundColor(Color.parseColor("#F3E5F5"));
                    break;

                case Constants.BOOKING_READY:
                    tvStatus.setText("Siap Diambil / Diantar");
                    tvStatus.setTextColor(Color.parseColor("#1565C0"));
                    tvStatus.setBackgroundColor(Color.parseColor("#E3F2FD"));
                    break;

                case Constants.BOOKING_COMPLETED:
                    tvStatus.setText("Selesai");
                    tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                    tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    break;

                case Constants.BOOKING_CANCELLED:
                    tvStatus.setText("Dibatalkan");
                    tvStatus.setTextColor(Color.parseColor("#C62828"));
                    tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                    break;

                default:
                    tvStatus.setText(status);
                    tvStatus.setTextColor(Color.parseColor("#555555"));
                    tvStatus.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    break;
            }
        }
    }
}