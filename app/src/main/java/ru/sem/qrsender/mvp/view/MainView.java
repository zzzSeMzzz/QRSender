package ru.sem.qrsender.mvp.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by Admin on 27.01.2018.
 */

public interface MainView extends MvpView {

    void onLoginSuccess(String hash);

    void showProgress(boolean show);

    @StateStrategyType(SkipStrategy.class)
    void showError(String text);
}
