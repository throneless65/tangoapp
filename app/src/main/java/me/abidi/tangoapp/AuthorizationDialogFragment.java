package me.abidi.tangoapp;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.home_connect.sdk.exceptions.HomeConnectException;
import com.home_connect.sdk.internal.util.TextUtil;
import com.home_connect.sdk.model.Permission;
import com.home_connect.sdk.property.RxBinder;
import com.home_connect.sdk.services.LoginService;

import java.util.Set;

import rx.functions.Action1;

/**
 * {@link DialogFragment} which is shown at the authorization process
 */
public class AuthorizationDialogFragment extends DialogFragment {

    private WebView mWebView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mWebView = new WebView(inflater.getContext());
        return mWebView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebView = null;
    }

    /**
     * set the {@link Permission} scopes and make a Token request via the {@link LoginService}
     */
    @Override
    public void onResume() {
        super.onResume();

        Set<Permission> scopes = Permission.createScope(
                Permission.IDENTIFY_APPLIANCE,
                Permission.CONTROL,
                Permission.CONTROL_APPLIANCE,
                Permission.MONITOR_APPLIANCE,
                Permission.OVEN,
                Permission.DISHWASHER,
                Permission.OVEN_CONTROL,
                Permission.OVEN_SETTINGS
        );

        RxBinder.bind(this
                , LoginService.create().requestToken(mWebView, scopes)
                , new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(getContext(), "token request successful!", Toast.LENGTH_LONG).show();
                    }
                }
                , new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof HomeConnectException) {
                            HomeConnectException exception = (HomeConnectException) throwable;
                            showErrorDialog(exception);
                            dismiss();
                        }
                    }
                }
        );
    }

    /**
     * Necessary call to unbind the {@link RxBinder} to avoid memory leaks
     */
    @Override
    public void onPause() {
        super.onPause();
        RxBinder.unbind(this);
    }

    protected void showErrorDialog(@Nullable HomeConnectException error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
}
