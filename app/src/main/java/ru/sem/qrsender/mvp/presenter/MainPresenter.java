package ru.sem.qrsender.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.sem.qrsender.App;
import ru.sem.qrsender.api.MainService;
import ru.sem.qrsender.mvp.view.MainView;
import ru.sem.qrsender.utils.HashUtil;

/**
 * Created by Admin on 27.01.2018.
 */

@InjectViewState
public class MainPresenter extends BasePresenter<MainView> {

    private String url;
    private MainService service;
    private static final String TAG = "MainPresenter";

    public MainPresenter(){
    }

    public void login(String login, String password){
        Context context = App.getInstance().getApplicationContext();
        SharedPreferences preferences = context.getSharedPreferences("conf", Context.MODE_PRIVATE);
        url ="http://" + preferences.getString("url","")+":"
                +preferences.getString("port","");
        try {
            service = App.getInstance().getServiceGenereator(url).getMainService();
        }catch (Exception e){
            e.printStackTrace();
            getViewState().showError(e.getMessage());
            return;
        }

        getViewState().showProgress(true);
        String hash = HashUtil.MD5(login+"."+password);
        Log.d(TAG, "login: hash="+hash);
        Disposable d = service.login(hash)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            String body = response.body().string();
                            Log.d(TAG, "auth: success response="+body);

                            if(body.equals("1")) {
                                getViewState().showProgress(false);
                                getViewState().onLoginSuccess(hash);
                            }else{
                                getViewState().showProgress(false);
                                getViewState().showError("Ошибка авторизации, неверное имя пользователя и пароль");
                            }
                        },
                        error -> {
                            Log.e(TAG, "auth: failed auth");
                            getViewState().showProgress(false);
                            getViewState().showError("Ошибка авторизации, попробуйте снова");
                        }
                );
        unsubscribeOnDestroy(d);
    }
}
