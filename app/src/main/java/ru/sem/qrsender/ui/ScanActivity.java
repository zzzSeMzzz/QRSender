package ru.sem.qrsender.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import github.nisrulz.qreader.QREader;
import ru.sem.qrsender.R;
import ru.sem.qrsender.mvp.presenter.ScanPresenter;
import ru.sem.qrsender.mvp.view.ScanView;

public class ScanActivity extends MvpAppCompatActivity implements ScanView{

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.btnScan)
    ImageButton btnScan;
    @BindView(R.id.tvSend)
    TextView tvSend;
    @BindView(R.id.tvWait)
    TextView tvWait;
    private QREader qrEader;
    private String hash;

    @InjectPresenter
    ScanPresenter presenter;

    @ProvidePresenter
    ScanPresenter provideScanPresenter () {
        return new ScanPresenter(getIntent().getStringExtra("hash"));
    }

    private static final String TAG = "ScanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        hash = getIntent().getStringExtra("hash");
        if(hash==null){
            showError("Произошла ошибка, перелогиньтесь");
            finish();
        }

        qrEader = new QREader.Builder(this, surfaceView, data -> {
            if(qrEader.isCameraRunning()){
                Log.d(TAG, "try stop qr reader");
                new Thread(() -> qrEader.stop()).start();
            }

            Log.d(TAG, "Value QR: " + data);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            presenter.sendQR(data);
        }
        ).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(surfaceView.getHeight())
                .width(surfaceView.getWidth())
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        qrEader.initAndStart(surfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrEader.releaseAndCleanup();
    }

    @Override
    public void showError(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setInfo(int sendCount, int wait) {
        tvSend.setText("Отправлено: "+sendCount);
        tvWait.setText("В очереди: "+wait);
    }

    @OnClick(R.id.btnScan)
    public void onClickScan(View v){
        if(qrEader.isCameraRunning()) return;
        qrEader.start();
    }
}
