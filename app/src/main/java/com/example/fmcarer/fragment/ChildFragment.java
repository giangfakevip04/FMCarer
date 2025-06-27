package com.example.fmcarer.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.*;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fmcarer.R;
import com.example.fmcarer.activity.LoginActivity;
import com.example.fmcarer.adapter.ChildAdapter;
import com.example.fmcarer.dialog.ChildFormDialog;
import com.example.fmcarer.model.Child;
import com.example.fmcarer.network.ApiClient;
import com.example.fmcarer.network.ApiService;
import com.example.fmcarer.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;

import retrofit2.*;

public class ChildFragment extends Fragment {

    private static final String TAG = "ChildFragment";
    private RecyclerView recyclerChildren;
    private FloatingActionButton fabAddChild;
    private ChildAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    public ChildFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated called");

        // ✅ Khởi tạo components
        recyclerChildren = view.findViewById(R.id.recyclerChildren);
        fabAddChild = view.findViewById(R.id.fabAddChild);

        // ✅ Khởi tạo SessionManager và ApiService (CHỈ MỘT LẦN)
        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getService();

        // ✅ Debug session info ngay khi khởi tạo
        sessionManager.debugSessionInfo();

        // ✅ Kiểm tra đăng nhập
        if (!sessionManager.isLoggedIn()) {
            Log.e(TAG, "User not logged in, redirecting to login");
            redirectToLogin();
            return;
        }

        Log.d(TAG, "User is logged in, token available: " + (sessionManager.getToken() != null));

        // ✅ Setup RecyclerView và Adapter
        setupRecyclerView();

        // ✅ Setup FAB
        setupFabAddChild();

        // ✅ Load children data
        loadChildren();
    }

    /**
     * Setup RecyclerView và Adapter
     */
    private void setupRecyclerView() {
        adapter = new ChildAdapter(new ArrayList<>(), new ChildAdapter.OnChildActionListener() {
            @Override
            public void onEdit(Child child) {
                Log.d(TAG, "Edit child: " + child.getName());
                showEditDialog(child);
            }

            @Override
            public void onDelete(Child child) {
                Log.d(TAG, "Delete child: " + child.getName());
                confirmDelete(child);
            }
        });

        recyclerChildren.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChildren.setAdapter(adapter);
    }

    /**
     * Setup FAB Add Child
     */
    private void setupFabAddChild() {
        fabAddChild.setOnClickListener(v -> {
            Log.d(TAG, "FAB clicked - showing add dialog");

            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(getContext(), "Cần đăng nhập lại", Toast.LENGTH_SHORT).show();
                redirectToLogin();
                return;
            }

            ChildFormDialog dialog = new ChildFormDialog(
                    requireContext(),
                    null, // null = thêm mới
                    apiService,
                    "Bearer " + sessionManager.getToken(),
                    new ChildFormDialog.OnSavedCallback() {
                        @Override
                        public void onSaved() {
                            Log.d(TAG, "Child saved callback - reloading children");
                            loadChildren();
                        }
                    }
            );
            dialog.show();
        });
    }

    /**
     * Load danh sách trẻ từ API
     */
    private void loadChildren() {
        if (!sessionManager.isLoggedIn()) {
            Log.e(TAG, "No token available for loading children");
            redirectToLogin();
            return;
        }

        String token = sessionManager.getToken();
        Log.d(TAG, "Loading children with token: " + token.substring(0, Math.min(10, token.length())) + "...");

        apiService.getChildren("Bearer " + token).enqueue(new Callback<List<Child>>() {
            @Override
            public void onResponse(Call<List<Child>> call, Response<List<Child>> response) {
                Log.d(TAG, "Load children response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Child> children = response.body();
                    Log.d(TAG, "Loaded " + children.size() + " children successfully");
                    adapter.setData(children);
                } else {
                    Log.e(TAG, "Failed to load children: " + response.message());

                    // ✅ Xử lý lỗi 401 Unauthorized
                    if (response.code() == 401) {
                        Log.e(TAG, "Token expired or invalid, redirecting to login");
                        sessionManager.clearSession();
                        redirectToLogin();
                        return;
                    }

                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }

                    Toast.makeText(getContext(), "Không thể tải danh sách trẻ: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Child>> call, Throwable t) {
                Log.e(TAG, "Network error loading children", t);
                Toast.makeText(getContext(), "Lỗi kết nối khi tải danh sách: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hiển thị dialog sửa trẻ
     */
    private void showEditDialog(Child child) {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(getContext(), "Cần đăng nhập lại", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        ChildFormDialog dialog = new ChildFormDialog(
                requireContext(),
                child, // không null = sửa
                apiService,
                "Bearer " + sessionManager.getToken(),
                new ChildFormDialog.OnSavedCallback() {
                    @Override
                    public void onSaved() {
                        Log.d(TAG, "Child updated callback - reloading children");
                        loadChildren();
                    }
                }
        );
        dialog.show();
    }

    /**
     * Xác nhận xóa trẻ
     */
    private void confirmDelete(Child child) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xoá trẻ")
                .setMessage("Bạn có chắc muốn xoá \"" + child.getName() + "\"?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteChild(child))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    /**
     * Xóa trẻ
     */
    private void deleteChild(Child child) {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(getContext(), "Cần đăng nhập lại", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        Log.d(TAG, "Deleting child: " + child.getName() + " with ID: " + child.getId());

        apiService.deleteChild("Bearer " + sessionManager.getToken(), child.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Delete child response code: " + response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG, "Child deleted successfully");
                    Toast.makeText(getContext(), "Đã xoá thành công", Toast.LENGTH_SHORT).show();
                    loadChildren();
                } else {
                    Log.e(TAG, "Failed to delete child: " + response.message());

                    // ✅ Xử lý lỗi 401 Unauthorized
                    if (response.code() == 401) {
                        Log.e(TAG, "Token expired during delete, redirecting to login");
                        sessionManager.clearSession();
                        redirectToLogin();
                        return;
                    }

                    Toast.makeText(getContext(), "Không thể xoá: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error deleting child", t);
                Toast.makeText(getContext(), "Lỗi kết nối khi xoá: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Chuyển về màn hình đăng nhập
     */
    private void redirectToLogin() {
        Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * Refresh data khi fragment được hiển thị lại
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        // ✅ Kiểm tra lại session khi resume
        if (sessionManager != null && sessionManager.isLoggedIn()) {
            Log.d(TAG, "Session still valid, refreshing data");
            loadChildren();
        } else {
            Log.w(TAG, "Session invalid on resume");
            redirectToLogin();
        }
    }
}