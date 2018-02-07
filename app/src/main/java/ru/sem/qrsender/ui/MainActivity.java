package ru.sem.qrsender.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.sem.qrsender.R;
import ru.sem.qrsender.mvp.presenter.MainPresenter;
import ru.sem.qrsender.mvp.view.MainView;
import ru.sem.qrsender.utils.PermissionHelper;



public class MainActivity extends MvpAppCompatActivity implements MainView,
        PermissionHelper.OnPermissionResultImpl{

    @BindView(R.id.progressBar2)
    ProgressBar progressBar;
    @BindView(R.id.edLogin)
    EditText edLogin;
    @BindView(R.id.edPassword)
    EditText edPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    private PermissionHelper permissionHelper;
    @InjectPresenter
    MainPresenter presenter;

    private final String[] perm = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE, Manifest.permission.CAMERA};
    private static final int PERM_ALL=7;

    private boolean validCred(){
        return (!edLogin.getText().toString().isEmpty()
                && !edPassword.getText().toString().isEmpty());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        permissionHelper = new PermissionHelper(this,
                findViewById(R.id.mainView), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            default:
                return super.onOptionsItemSelected(item);
            case R.id.action_settings:
                showSettingsDialog();
                return true;
        }
    }

    @Override
    public void onLoginSuccess(String hash) {
        //showError("success");
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("hash", hash);
        startActivity(intent);
    }

    @Override
    public void showError(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(boolean show) {
        btnLogin.setEnabled(!show);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPermissionsResult(int requestCode, boolean isGranted) {
        switch (requestCode) {
            case PERM_ALL:
                if (isGranted) {
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    presenter.login(edLogin.getText().toString(), edPassword.getText().toString());
                } else {
                    Toast.makeText(this, "Для работы приложения " +
                            "требуются все запрашиваемые разрешения", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onPermissionsResult(requestCode, permissions, grantResults);
    }



    @OnClick(R.id.btnLogin)
    public void onClickLogin(View v){
        if(!validCred()){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        permissionHelper.requestPermissions(perm, PERM_ALL,
                "Для работы приложения необходимы следующие разрешения");
    }

    private void showSettingsDialog(){
        SharedPreferences preferences = getSharedPreferences("conf", Context.MODE_PRIVATE);
        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.dialog_settings, null);
        EditText edHost = deleteDialogView.findViewById(R.id.edHost);
        EditText edPort = deleteDialogView.findViewById(R.id.edPort);
        edHost.setText(preferences.getString("url","http://89.169.192.8"));
        edPort.setText(preferences.getString("port","7999"));
        final AlertDialog deleteDialog =
                new AlertDialog.Builder(this)
                .setPositiveButton("ok", (dialogInterface, i) -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("url", edHost.getText().toString());
                    editor.putString("port", edPort.getText().toString());
                    editor.apply();
                })
                .setNegativeButton("Отмена", null)
                .create();
        deleteDialog.setView(deleteDialogView);

        deleteDialog.show();
    }

}
