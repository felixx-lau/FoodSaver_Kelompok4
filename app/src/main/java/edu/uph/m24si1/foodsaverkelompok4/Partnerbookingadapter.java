package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PartnerBookingAdapter extends RecyclerView.Adapter<PartnerBookingAdapter.ViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private OnStatusUpdateListener listener;

    public interface OnStatusUpdateListener {
        void onUpdate(Booking booking, String newStatus);
    }

    public PartnerBookingAdapter(Context context, List<Booking> bookingList,
                                 OnStatusUpdateListener listener) {
        this.context = context;
        this.bookingList = bookingList;
        this.listener = listener;
    }

    public void updateList(List<Booking> newList) {
        this.bookingList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_partner_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFoodName, tvUserName, tvStatus, tvMethodTime, tvQuantityInfo;
        Button btnUpdateStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMethodTime = itemView.findViewById(R.id.tvMethodTime);
            tvQuantityInfo = itemView.findViewById(R.id.tvQuantityInfo);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }

        public void bind(Booking booking) {
            tvFoodName.setText(booking.getFoodName() != null ? booking.getFoodName() : "-");
            tvUserName.setText("Pemesan: " + (booking.getUserName() != null ? booking.getUserName() : "-"));
            tvQuantityInfo.setText(booking.getQuantity() + " porsi");

            String methodIcon = Constants.METHOD_PICKUP.equals(booking.getDeliveryMethod())
                    ? "Pickup" : "Delivery";
            tvMethodTime.setText(methodIcon + " - " + booking.getScheduledTimeSlot());

            // Status badge dan tombol aksi berdasarkan status saat ini
            switch (booking.getStatus()) {
                case Constants.BOOKING_PENDING:
                    tvStatus.setText("Menunggu");
                    tvStatus.setTextColor(Color.parseColor("#E65100"));
                    tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
                    btnUpdateStatus.setText("Tandai Siap");
                    btnUpdateStatus.setVisibility(View.VISIBLE);
                    btnUpdateStatus.setOnClickListener(v ->
                            listener.onUpdate(booking, Constants.BOOKING_READY));
                    break;

                case Constants.BOOKING_READY:
                    tvStatus.setText("Siap diambil");
                    tvStatus.setTextColor(Color.parseColor("#1565C0"));
                    tvStatus.setBackgroundColor(Color.parseColor("#E3F2FD"));
                    btnUpdateStatus.setText("Tandai Selesai");
                    btnUpdateStatus.setVisibility(View.VISIBLE);
                    btnUpdateStatus.setOnClickListener(v ->
                            listener.onUpdate(booking, Constants.BOOKING_COMPLETED));
                    break;

                case Constants.BOOKING_COMPLETED:
                    tvStatus.setText("Selesai");
                    tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                    tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    btnUpdateStatus.setVisibility(View.GONE);
                    break;

                case Constants.BOOKING_CANCELLED:
                    tvStatus.setText("Dibatalkan");
                    tvStatus.setTextColor(Color.parseColor("#C62828"));
                    tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                    btnUpdateStatus.setVisibility(View.GONE);
                    break;
            }
        }
    }
}