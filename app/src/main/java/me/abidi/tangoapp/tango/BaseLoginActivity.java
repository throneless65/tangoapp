package me.abidi.tangoapp.tango;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.home_connect.sdk.exceptions.HomeConnectException;
import com.home_connect.sdk.internal.util.TextUtil;
import com.home_connect.sdk.property.RxBinder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.abidi.tangoapp.R;

/**
 * The BaseActivity to provide all the necessary methods for all other activities
 */
public abstract class BaseLoginActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Toast toaster;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        doPostSetContentViewSetup();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        doPostSetContentViewSetup();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);

        doPostSetContentViewSetup();
    }

    private void doPostSetContentViewSetup() {
        ButterKnife.bind(this);
//        /setSupportActionBar(toolbar);
    }

    protected Toolbar getToolbar() {
        return toolbar;
    }

    protected void showToastMessage(String msg) {
        showToastMessage(msg, Toast.LENGTH_SHORT);
    }

    protected void showToastMessage(String msg, int duration) {
        if (toaster == null) {
            toaster = Toast.makeText(this, "", duration);
        }
        toaster.setDuration(duration);
        toaster.setText(msg);
        toaster.show();
    }

    protected void showErrorDialog(@Nullable HomeConnectException error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error);
        if (error != null && error.apiError != null && !TextUtil.isEmpty(error.apiError.description)) {
            builder.setMessage(error.apiError.description);
        } else if (error != null && !TextUtil.isEmpty(error.description)) {
            builder.setMessage(error.description);
        } else if (error != null && !TextUtil.isEmpty(error.httpMessage)) {
            builder.setMessage(error.httpMessage);
        } else {
            builder.setMessage(R.string.unknown_error);
        }

        builder.setPositiveButton(R.string.ok, null);
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }
    /**
     * Necessary call to unbind the {@link RxBinder} to avoid memory leaks
     */
    @Override
    protected void onPause() {
        super.onPause();
        RxBinder.unbind(this);
    }
}