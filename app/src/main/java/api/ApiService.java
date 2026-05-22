package api;

import models.LoginRequest;
import models.LoginResponse;
import models.PostsResponse;
import models.PostResponse;
import models.CreatePost;
import models.DeleteResponse;
import models.Register;
import models.RegisterResponse;
import models.ProfileResponse;
import models.UpdateProfile;
import models.UpdateProfileResponse;
import models.FriendsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("/api/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("/api/register")
    Call<RegisterResponse> registerUser(@Body Register request);

    @GET("/api/posts")
    Call<PostsResponse> getAllPosts();

    @POST("/api/posts")
    Call<PostResponse> createPost(@Body CreatePost request);

    @DELETE("/api/posts/{post_id}")
    Call<DeleteResponse> deletePost(@Path("post_id") int postId);

    @GET("/api/posts/user/{user_id}")
    Call<PostsResponse> getUserPosts(@Path("user_id") int userId);

    @GET("/api/users/{user_id}/profile")
    Call<ProfileResponse> getUserProfile(@Path("user_id") int userId);

    @PATCH("/api/users/{user_id}/profile")
    Call<UpdateProfileResponse> patchUserProfile(@Path("user_id") int userId, @Body UpdateProfile request);

    @GET("/api/users/{user_id}/friends")
    Call<FriendsResponse> getUserFriends(@Path("user_id") int userId);
}