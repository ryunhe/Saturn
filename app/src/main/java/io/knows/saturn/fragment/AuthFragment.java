package io.knows.saturn.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.renn.rennsdk.RennClient;

import javax.inject.Inject;

import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.activity.MainActivity;
import io.knows.saturn.activity.SignupActivity;
import io.knows.saturn.helper.DeviceEnvHelper;
import io.knows.saturn.model.User;
import io.knows.saturn.service.SamuiService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-22.
 */
public class AuthFragment extends Fragment {
    @Inject
    RennClient mRennClient;
    @Inject
    SamuiService mSamuiService;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_auth, container, false);
        inject(layout);

        return layout;
    }

    @OnClick(R.id.button_renren)
    void renren() {

        mRennClient.setLoginListener(new RennClient.LoginListener() {
            @Override
            public void onLoginSuccess() {
                Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();

                mSamuiService.authRenren(DeviceEnvHelper.getDeviceId(getActivity()), mRennClient.getUid().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(authResponse -> {
                            User user = authResponse.getAuth().user;

                            Timber.d(authResponse.getAuth().sessionCode);
                            Timber.d(user.nickname);

                            if (null == user.nickname) {
                                startActivity(new Intent(getActivity(), SignupActivity.class));
                            } else {
                                startActivity(new Intent(getActivity(), MainActivity.class));
                            }

                        });
            }

            @Override
            public void onLoginCanceled() {
                Toast.makeText(getActivity(), "登录失败", Toast.LENGTH_SHORT).show();
            }
        });

        mRennClient.login(getActivity());
    }
}
