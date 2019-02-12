package ru.sem.qrsender.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.sem.qrsender.R;
import ru.sem.qrsender.mvp.presenter.ScanPresenter;
import ru.sem.qrsender.mvp.view.ScanView;

public class ScanActivity extends MvpAppCompatActivity implements ScanView{

    @BindView(R.id.btnScan)
    ImageButton btnScan;
    @BindView(R.id.tvSend)
    TextView tvSend;
    @BindView(R.id.tvWait)
    TextView tvWait;
    @BindView(R.id.textResponse)
    TextView textResponse;

    private String hash;

    @InjectPresenter
    ScanPresenter presenter;

    private MenuItem sendQrMenuItem;

    @BindView(R.id.dbv_barcode)
    DecoratedBarcodeView dbvScanner;

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
    }

    @Override
    public void initQR(){
        Log.d(TAG, "initQR: initialization");
        /*qrCodeReaderView.setOnQRCodeReadListener((text, points) -> {
            //Log.d(TAG, "onQRCodeRead: "+text);
            qrCodeReaderView.stopCamera();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            qrCodeReaderView.setQRDecodingEnabled(false);
            presenter.sendQR(text);
        });

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);
        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);
        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);
        // Use this function to set front camera preview
        //qrCodeReaderView.setFrontCamera();
        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();*/

        dbvScanner.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                String code = result.getText();
                Log.d(TAG, "barcodeResult: "+code);
                pauseScanner();
                presenter.sendQR(code);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeScanner();
    }

    protected void resumeScanner() {
        //isScanDone = false;
        if (!dbvScanner.isActivated())
            dbvScanner.resume();
        Log.d(TAG, "paused: false");
    }

    protected void pauseScanner() {
        dbvScanner.pause();
    }


    @Override
    protected void onPause() {
        super.onPause();
        pauseScanner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_main, menu);
        sendQrMenuItem = menu.findItem(R.id.action_send_que);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            default:
                return super.onOptionsItemSelected(item);
            case R.id.action_send_que:
                presenter.sendQue();
                return true;
        }
    }

    @Override
    public void showError(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setMenuEnabled(boolean enabled) {
        if(sendQrMenuItem!=null) sendQrMenuItem.setEnabled(enabled);
    }

    @Override
    public void setInfo(int sendCount, int wait) {
        tvSend.setText("Отправлено: "+sendCount);
        tvWait.setText("В очереди: "+wait);
    }

    @OnClick({R.id.btnScan, R.id.textResponse})
    public void onClickScan(View v){
        /*qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.startCamera();*/

        resumeScanner();
        presenter.getViewState().setResponse("");
    }

    @Override
    public void setResponse(String response) {
        if(response==null||response.isEmpty()){
            textResponse.setVisibility(View.GONE);
        }else{
            textResponse.setVisibility(View.VISIBLE);
            textResponse.setText(response);
        }
    }
}
