package kr.co.gmgpayment.app.api;

import kr.co.gmgpayment.app.login.LoginDTO;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API_Interface {

    //회원가입
    @FormUrlEncoded
    @POST("/api/join_user.php")
    Call<BaseDTO> join_user(
            @Field("user_phone") String user_phone,
            @Field("passwd") String passwd,
            @Field("nickname") String nickname,
            @Field("agree_info") String agree_info,
            @Field("agree_sms") String agree_sms,
            @Field("agree_message") String agree_message,
            @Field("agree_gps") String agree_gps);

    //로그인
    @FormUrlEncoded
    @POST("/api/login_chk.php")
    Call<LoginDTO> login_chk(
            @Field("store_id") String store_id,
            @Field("passwd") String passwd);


    //주문서 스캔 리스트
//    @POST("/api/sf_mnfct_list.php")
//    Call<ScanListDTO> sf_mnfct_list();




}
