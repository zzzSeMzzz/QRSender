package ru.sem.qrsender.mvp.view;

import com.arellomobile.mvp.MvpView;

/**
 * Created by Admin on 27.01.2018.
 */

public interface ScanView extends MvpView {


    void showError(String text);

    void setInfo(int sendCount, int wait);

    void initQR();

    void setMenuEnabled(boolean enabled);

    void setResponse(String response);
}
