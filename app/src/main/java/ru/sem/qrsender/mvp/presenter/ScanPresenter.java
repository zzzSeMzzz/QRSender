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
import ru.sem.qrsender.mvp.model.QRContainer;
import ru.sem.qrsender.mvp.view.ScanView;

/**
 * Created by Admin on 27.01.2018.
 */

@InjectViewState
public class ScanPresenter extends BasePresenter<ScanView> {

    private static final String TAG = "ScanPresenter";
    private String url;
    private MainService service;
    private QRContainer qrContainer;
    private String hash;

    public ScanPresenter(String hash) {
        Context context = App.getInstance().getApplicationContext();
        SharedPreferences preferences = context.getSharedPreferences("conf", Context.MODE_PRIVATE);
        url = "http://"+preferences.getString("url","")+":"
                +preferences.getString("port","");
        service = App.getInstance().getServiceGenereator(url).getMainService();
        this.hash=hash;
        qrContainer=new QRContainer(hash);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initQR();
    }


    public void sendQR(String qr){
        Log.d(TAG, "login: hash="+hash);
        Disposable d = service.sendQr(hash, qr)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            String body = response.body().string();
                            if(body.equals("1")) {
                                Log.d(TAG, "success send qr="+body);
                                qrContainer.incSendCount();
                                getViewState().setInfo(qrContainer.getSendCount(), qrContainer.getCountQue());
                                getViewState().showError("Голос учтен");
                            }else{
                                Log.e(TAG, "failure send qr="+body);
                                qrContainer.addFirst(qr);
                                getViewState().setInfo(qrContainer.getSendCount(), qrContainer.getCountQue());
                            }
                        },
                        error -> {
                            Log.e(TAG, "auth: failed auth");
                            //getViewState().showError("Ошибка авторизации, попробуйте снова");
                            qrContainer.addFirst(qr);
                            getViewState().setInfo(qrContainer.getSendCount(), qrContainer.getCountQue());
                        }
                );
        unsubscribeOnDestroy(d);
    }

    public void sendQue(){
        if(qrContainer.getCountQue()==0){
            getViewState().showError("В очереди пусто");
            getViewState().setMenuEnabled(true);
            return;
        }
        //getViewState().showError("Отправка очереди...");
        getViewState().setMenuEnabled(false);
        Log.d(TAG, "sendQue: hash="+hash);
        String qr = qrContainer.getLast();
        Log.d(TAG, "sendQue: qr="+qr);
        Disposable d = service.sendQr(hash, qr)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            String body = response.body().string();
                            if(body.equals("1")) {
                                Log.d(TAG, "success send qr="+body);
                                qrContainer.incSendCount();
                                getViewState().setInfo(qrContainer.getSendCount(), qrContainer.getCountQue());
                                qrContainer.getQrs().pollLast();
                                if(qrContainer.getCountQue()!=0) {
                                    sendQue();
                                }else{
                                    getViewState().setMenuEnabled(true);
                                }
                            }else{
                                Log.e(TAG, "failure send qr="+body);
                                //qrContainer.addFirst(qr);
                                getViewState().setInfo(qrContainer.getSendCount(), qrContainer.getCountQue());
                                getViewState().setMenuEnabled(true);
                                getViewState().showError("Ошибка отправка очереди+\n"+body);
                            }
                        },
                        error -> {
                            Log.e(TAG, "auth: failed auth");
                            //getViewState().showError("Ошибка авторизации, попробуйте снова");
                            qrContainer.addFirst(qr);
                            getViewState().setInfo(qrContainer.getSendCount(), qrContainer.getCountQue());
                        }
                );
        unsubscribeOnDestroy(d);
    }
}
