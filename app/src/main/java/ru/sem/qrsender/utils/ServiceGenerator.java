package ru.sem.qrsender.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import ru.sem.qrsender.api.MainService;

/**
 * Created by Admin on 27.01.2018.
 */

public class ServiceGenerator {

    public final String API_BASE_URL;

    public ServiceGenerator(String urlAPi) {
        API_BASE_URL = urlAPi;
    }

    private Retrofit.Builder getRetrofit(){
        Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return  builder;
    }

    public MainService getMainService(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = getRetrofit().client(client).build();
        return retrofit.create(MainService.class);
    }
}
