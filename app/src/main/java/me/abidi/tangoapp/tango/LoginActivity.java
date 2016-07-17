package me.abidi.tangoapp.tango;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.home_connect.sdk.property.RxBinder;
import com.home_connect.sdk.services.LoginService;

import butterknife.OnClick;
import me.abidi.tangoapp.AuthorizationDialogFragment;
import me.abidi.tangoapp.MainActivity;
import me.abidi.tangoapp.R;
import rx.functions.Action1;

/**
 * First Activity which ist started and shows the loginScreen
 */
public class LoginActivity extends BaseLoginActivity {

    private LoginService loginService;

    /**
     * Normal Android onCreate method which initialize our {@link LoginService} reference
     * @param savedInstanceState saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginService = LoginService.create();
    }

    /**
     * Handles login button onClick and uses {@link LoginService} authorization method
     * @param v View
     */
    @OnClick(R.id.loginBtn)
    public void onLoginClick(View v) {
        if (loginService.getAuthorized().get()) {
            Log.e("LoginActivity", "Login on click");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Log.e("LoginActivity", "Authorization dialog");
            new AuthorizationDialogFragment().show(getSupportFragmentManager(), "");
        }
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Normal android onResume method which uses the {@link LoginService} authorization check
     */
    @Override
    protected void onResume() {
        super.onResume();

        RxBinder.bind(
                this,
                loginService.getAuthorized().observe(),
                new Action1<Boolean>() {
                    @Override
                    public void call(Boolean value) {
                        boolean wasLoggedIn = value != null && value;
                        if (wasLoggedIn)  {
                            Log.e("LoginActivity", "resuming login");
                            showMainActivity();
                        }
                    }
                }
        );
    }
}
