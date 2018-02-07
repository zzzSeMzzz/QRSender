package ru.sem.qrsender.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Admin on 27.01.2018.
 */

public interface MainService {

    @GET("/")
    Observable<Response<ResponseBody>> login(@Query("hash") String hash);

    @GET("/")
    Observable<Response<ResponseBody>> sendQr(@Query("hash") String hash,
                                              @Query("qr") String qr);
}
