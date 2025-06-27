package com.example.fmcarer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import androidx.annotation.NonNull;

import com.example.fmcarer.R;
import com.example.fmcarer.model.Child;
import com.example.fmcarer.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildFormDialog extends Dialog {

    private static final String TAG = "ChildFormDialog";
    private EditText inputName, inputDob;
    private RadioGroup radioGender;
    private Button btnSave, btnDelete, btnCancel;
    private TextView dialogTitle;

    private Child child;
    private ApiService apiService;
    private String token;
    private OnSavedCallback listener;

    public interface OnSavedCallback {
        void onSaved();
    }

    public ChildFormDialog(@NonNull Context context, Child child, ApiService apiService, String token, OnSavedCallback listener) {
        super(context);
        this.child = child;
        this.apiService = apiService;
        this.token = token;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_child_form);

        // Thiết lập kích thước và vị trí dialog
        setupDialogWindow();

        initViews();
        setupUIMode();
        fillData();
        setupButtonListeners();
    }

    private void initViews() {
        inputName = findViewById(R.id.inputChildName);
        inputDob = findViewById(R.id.inputChildDob);
        radioGender = findViewById(R.id.radioGender);
        btnSave = findViewById(R.id.btnSaveChild);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);
        dialogTitle = findViewById(R.id.dialogTitle);
    }

    private void setupUIMode() {
        if (child != null) {
            // Chế độ sửa
            dialogTitle.setText("Sửa thông tin trẻ");
            btnSave.setText("Cập nhật");
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            // Chế độ thêm mới
            dialogTitle.setText("Thêm trẻ mới");
            btnSave.setText("Thêm");
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void fillData() {
        if (child != null) {
            inputName.setText(child.getName());
            // Chuyển đổi từ ISO format sang dd/MM/yyyy để hiển thị
            inputDob.setText(formatDateForDisplay(child.getBirthDate()));
            if ("male".equals(child.getGender())) {
                radioGender.check(R.id.radioMale);
            } else {
                radioGender.check(R.id.radioFemale);
            }
        } else {
            // Mặc định chọn Nam
            radioGender.check(R.id.radioMale);
        }
    }

    private void setupButtonListeners() {
        // Nút Cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Nút Save/Update
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSaveAction();
            }
        });

        // Nút Delete
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteAction();
            }
        });
    }

    private void handleSaveAction() {
        String name = inputName.getText().toString().trim();
        String dob = inputDob.getText().toString().trim();
        String gender = radioGender.getCheckedRadioButtonId() == R.id.radioMale ? "male" : "female";

        if (name.isEmpty() || dob.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi ngày sinh từ dd/MM/yyyy sang ISO format
        String isoDate = convertToISOFormat(dob);
        if (isoDate == null) {
            Toast.makeText(getContext(), "Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Saving child - Name: " + name + ", DOB: " + isoDate + ", Gender: " + gender);

        if (child == null) {
            // THÊM TRẺ MỚI
            Child newChild = new Child();
            newChild.setName(name);
            newChild.setBirthDate(isoDate);
            newChild.setGender(gender);

            Log.d(TAG, "Creating new child with token: " + token);
            apiService.createChild(token, newChild).enqueue(new Callback<Child>() {
                @Override
                public void onResponse(Call<Child> call, Response<Child> response) {
                    Log.d(TAG, "Create child response code: " + response.code());
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Child created successfully");
                        Toast.makeText(getContext(), "Đã thêm trẻ", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onSaved();
                        }
                        dismiss();
                    } else {
                        Log.e(TAG, "Failed to create child: " + response.message());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        Toast.makeText(getContext(), "Không thể thêm trẻ: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Child> call, Throwable t) {
                    Log.e(TAG, "Network error creating child", t);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // CẬP NHẬT TRẺ
            child.setName(name);
            child.setBirthDate(isoDate);
            child.setGender(gender);

            Log.d(TAG, "Updating child with ID: " + child.getId());
            apiService.updateChild(token, child.getId(), child).enqueue(new Callback<Child>() {
                @Override
                public void onResponse(Call<Child> call, Response<Child> response) {
                    Log.d(TAG, "Update child response code: " + response.code());
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Child updated successfully");
                        Toast.makeText(getContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onSaved();
                        }
                        dismiss();
                    } else {
                        Log.e(TAG, "Failed to update child: " + response.message());
                        Toast.makeText(getContext(), "Không thể cập nhật: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Child> call, Throwable t) {
                    Log.e(TAG, "Network error updating child", t);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void handleDeleteAction() {
        if (child == null) return;

        // Hiển thị dialog xác nhận xóa
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa trẻ \"" + child.getName() + "\" không?")
                .setPositiveButton("Xóa", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        deleteChild();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteChild() {
        Log.d(TAG, "Deleting child with ID: " + child.getId());
        apiService.deleteChild(token, child.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Delete child response code: " + response.code());
                if (response.isSuccessful()) {
                    Log.d(TAG, "Child deleted successfully");
                    Toast.makeText(getContext(), "Đã xóa trẻ", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onSaved();
                    }
                    dismiss();
                } else {
                    Log.e(TAG, "Failed to delete child: " + response.message());
                    Toast.makeText(getContext(), "Không thể xóa: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error deleting child", t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDialogWindow() {
        Window window = getWindow();
        if (window != null) {
            // Thiết lập background trong suốt
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Lấy kích thước màn hình
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9); // 90% chiều rộng màn hình
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Chiều cao tự động
            layoutParams.gravity = Gravity.CENTER;

            window.setAttributes(layoutParams);
        }
    }

    /**
     * Chuyển đổi từ dd/MM/yyyy sang ISO format (yyyy-MM-dd'T'HH:mm:ss.SSS'Z')
     */
    private String convertToISOFormat(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error converting date format", e);
            return null;
        }
    }

    /**
     * Chuyển đổi từ ISO format sang dd/MM/yyyy để hiển thị
     */
    private String formatDateForDisplay(String isoDate) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = isoFormat.parse(isoDate);
            return displayFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date for display", e);
            return isoDate; // Trả về nguyên bản nếu lỗi
        }
    }
}