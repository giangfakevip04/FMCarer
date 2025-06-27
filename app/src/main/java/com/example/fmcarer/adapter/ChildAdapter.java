package com.example.fmcarer.adapter;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fmcarer.R;
import com.example.fmcarer.model.Child;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

    /**
     * Interface để lắng nghe sự kiện từ item: sửa hoặc xoá
     */
    public interface OnChildActionListener {
        void onEdit(Child child);
        void onDelete(Child child);
    }

    private List<Child> childList;
    private final OnChildActionListener listener;

    public ChildAdapter(List<Child> childList, OnChildActionListener listener) {
        this.childList = childList;
        this.listener = listener;
    }

    public void setData(List<Child> newList) {
        this.childList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childList.get(position);
        holder.bind(child);
    }

    @Override
    public int getItemCount() {
        return childList != null ? childList.size() : 0;
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        private final TextView textChildName, textChildDob;
        private final ImageView imageAvatar;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            textChildName = itemView.findViewById(R.id.textChildName);
            textChildDob = itemView.findViewById(R.id.textChildDob);
            imageAvatar = itemView.findViewById(R.id.imageAvatar); // cần có trong item_child.xml
        }

        public void bind(Child child) {
            textChildName.setText(child.getName());
            textChildDob.setText("Ngày sinh: " + formatDate(child.getBirthDate()));

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(child);
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) listener.onDelete(child);
                return true;
            });
        }

        private String formatDate(String isoDate) {
            try {
                SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return output.format(iso.parse(isoDate));
            } catch (Exception e) {
                return isoDate;
            }
        }
    }
}
