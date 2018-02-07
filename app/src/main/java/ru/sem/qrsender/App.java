package ru.sem.qrsender;

import android.app.Application;

import ru.sem.qrsender.utils.ServiceGenerator;

/**
 * Created by SeM on 02.10.2017.
 */

public class App extends Application {

    private static App instance;

    public ServiceGenerator serviceGenerator;

    public static App getInstance() {
        if(instance==null) instance = new App();
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public ServiceGenerator getServiceGenereator(String apiUrl){
        return new ServiceGenerator(apiUrl);
    }

}
