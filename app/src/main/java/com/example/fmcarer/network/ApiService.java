package com.example.fmcarer.network;

import com.example.fmcarer.model.Children;
import com.example.fmcarer.model.Post;
import com.example.fmcarer.model_call_api.OTPRequest;
import com.example.fmcarer.model_call_api.PostRequest;
import com.example.fmcarer.model_call_api.SubUserLoginRequest;
import com.example.fmcarer.model_call_api.SubUserRequest;
import com.example.fmcarer.model_call_api.UserRequest;
import com.example.fmcarer.model_call_api.UserUpdateRequest;
import com.example.fmcarer.response.ApiResponse;
import com.example.fmcarer.response.CareScheduleResponse;
import com.example.fmcarer.response.ChildrenResponse;
import com.example.fmcarer.response.ImageUploadResponse;
import com.example.fmcarer.response.MultiImageUploadResponse;
import com.example.fmcarer.response.OTPResponse;
import com.example.fmcarer.response.PostResponse;
import com.example.fmcarer.response.SingleCareScheduleResponse;
import com.example.fmcarer.response.UserListResponse;
import com.example.fmcarer.response.UserResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // 🔒 USER AUTHENTICATION
    @GET("/api/users")
    Call<UserListResponse> getAllUsers(); // ✅ Trả về danh sách users (ẩn password)

    @POST("/api/users/register")
    Call<UserResponse> registerUser(@Body UserRequest request); // ✅ Đăng ký

    @POST("/api/users/login")
    Call<UserResponse> loginUser(@Body UserRequest request); // ✅ Đăng nhập chính

    @POST("/api/users/send-otp")
    Call<OTPResponse> sendOtp(@Body OTPRequest request); // ✅ Gửi OTP

    @POST("/api/users/update")
    Call<UserResponse> updateUser(@Body UserUpdateRequest request); // ✅ Cập nhật user

    @Multipart
    @POST("/api/users/upload-avatar")
    Call<UserResponse> uploadImage(
            @Part("userId") RequestBody userId,
            @Part MultipartBody.Part avatar
    ); // ✅ Upload avatar

    // 🔧 Sub-user (tài khoản phụ) - Đã đồng bộ hoàn toàn với Backend Router và Controller
    @POST("/api/users/login-subuser")
    Call<UserResponse> loginSubUser(@Body SubUserLoginRequest request); // ✅ Đăng nhập tài khoản phụ

    @POST("/api/users/subuser/create-or-update")
    Call<ApiResponse> createOrUpdateSubUser(
            @Header("Authorization") String bearerToken, // Thêm header xác thực
            @Body SubUserRequest subUser
    ); // ✅ Thêm/sửa sub user

    @GET("/api/users/subusers/parent/{parentId}")
    Call<UserListResponse> getAllSubusersByParentId(
            @Header("Authorization") String bearerToken, // Thêm header xác thực
            @Path("parentId") String parentId
    ); // ✅ Lấy tất cả danh sách subuser của một parent

    @GET("/api/users/subuser/{subuserId}")
    Call<UserResponse> getSubuserById(
            @Header("Authorization") String bearerToken, // Thêm header xác thực
            @Path("subuserId") String subuserId
    ); // ✅ Lấy thông tin một subuser cụ thể

    @DELETE("/api/users/subuser/{subuserId}")
    Call<ApiResponse> deleteSubuser(
            @Header("Authorization") String bearerToken, // Thêm header xác thực
            @Path("subuserId") String subuserId
    ); // ✅ Xóa một subuser


    // ✅ 1. Lấy danh sách trẻ của người dùng (dựa theo token)
    @GET("/api/children/my")
    Call<ChildrenResponse> getChildrenByUser(@Header("Authorization") String bearerToken);

    // ✅ 2. Lấy chi tiết 1 trẻ theo ID
    @GET("/api/children/{childId}")
    Call<Children> getChildById(@Header("Authorization") String bearerToken, @Path("childId") String childId);

    // ✅ 3. Thêm trẻ mới (dựa theo token)
    @POST("/api/children")
    Call<Children> addChild(@Header("Authorization") String bearerToken, @Body Children child);

    // ✅ 4. Cập nhật thông tin trẻ
    @PUT("/api/children/{childId}")
    Call<Children> updateChild(@Header("Authorization") String bearerToken, @Path("childId") String childId, @Body Children updatedChild);

    // ✅ 5. Xóa trẻ
    @DELETE("/api/children/{childId}")
    Call<Void> deleteChild(@Header("Authorization") String bearerToken, @Path("childId") String childId);


    // ✅ Care Schedules / Reminders


    // ✅ Tạo reminder mới
    // Backend: router.post('/', requireAuth, controller.createReminder);
    @POST("/api/reminders")
    Call<SingleCareScheduleResponse> createReminder(
            @Header("Authorization") String token,
            @Body Map<String, Object> reminderData // Đổi tên cho rõ ràng hơn
    );

    // ✅ Lấy toàn bộ reminder của user (từ token)
    // Backend: router.get('/', requireAuth, controller.getRemindersByUser);
    @GET("/api/reminders")
    Call<CareScheduleResponse> getAllReminders(
            @Header("Authorization") String token
    );

    // ✅ Lấy reminder theo ID (có kiểm tra user)
    // Backend: router.get('/:id', requireAuth, controller.getReminderById);
    @GET("/api/reminders/{id}")
    Call<SingleCareScheduleResponse> getReminderById(
            @Header("Authorization") String token,
            @Path("id") String reminderId // Tên @Path "id" khớp với backend
    );

    // ✅ Cập nhật reminder (có kiểm tra user)
    // Backend: router.put('/:id', requireAuth, controller.updateReminder);
    @PUT("/api/reminders/{id}")
    Call<SingleCareScheduleResponse> updateReminder(
            @Header("Authorization") String token,
            @Path("id") String reminderId,
            @Body Map<String, Object> updateData
    );

    // ✅ Xoá reminder (có kiểm tra user)
    // Backend: router.delete('/:id', requireAuth, controller.deleteReminder);
    @DELETE("/api/reminders/{id}")
    Call<ApiResponse> deleteReminder(
            @Header("Authorization") String token,
            @Path("id") String reminderId
    );

    // ✅ Đánh dấu hoàn thành (có kiểm tra user)
    // Backend: router.put('/:id/complete', requireAuth, controller.completeReminder);
    @PUT("/api/reminders/{id}/complete")
    Call<SingleCareScheduleResponse> completeReminder(
            @Header("Authorization") String token,
            @Path("id") String reminderId // Đổi tên tham số cho nhất quán
    );

    // ✅ Lấy danh sách reminder theo childId (có kiểm tra user)
    // Backend: router.get('/by-child/:childId', requireAuth, controller.getRemindersByChild);
    @GET("/api/reminders/by-child/{childId}")
    Call<CareScheduleResponse> getRemindersByChild(
            @Header("Authorization") String token,
            @Path("childId") String childId // Tên @Path "childId" khớp với backend
    );
    // ✅ Tạo bài viết mới - Đồng bộ với backend
    @POST("/api/posts")
    Call<PostResponse> createPost(@Body PostRequest postRequest);

    // ✅ Lấy tất cả bài viết - Đồng bộ với backend
    @GET("/api/posts")
    Call<List<Post>> getAllPosts();

    // ⚠️ Lấy danh sách bài viết theo userId (lọc theo user)
    // Giả sử backend sẽ lắng nghe query param "userId" trên endpoint /api/posts
    @GET("/api/posts")
    Call<List<Post>> getPostsByUserId(@Query("userId") String userId);

    // ✅ Cập nhật bài viết - Đồng bộ với backend
    @PUT("/api/posts/{postId}")
    Call<Post> updatePost(@Path("postId") String postId, @Body Post updatedPost);

    // ✅ Xóa bài viết - Đồng bộ với backend
    @DELETE("/api/posts/{postId}")
    Call<ApiResponse> deletePost(@Path("postId") String postId);

    //✅ Upload một ảnh
    @Multipart
    @POST("/api/upload")
    Call<ImageUploadResponse> uploadSingleImage(@Part MultipartBody.Part image);

    // ✅ Upload nhiều ảnh cùng lúc
    @Multipart
    @POST("/api/upload-multiple")
    Call<MultiImageUploadResponse> uploadMultipleImages(@Part List<MultipartBody.Part> images);

}

