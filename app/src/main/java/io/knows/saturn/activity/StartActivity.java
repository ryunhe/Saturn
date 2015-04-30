package io.knows.saturn.activity;

import android.content.Intent;
import android.os.Bundle;

import com.renn.rennsdk.RennClient;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import io.knows.saturn.R;
import io.knows.saturn.fragment.AuthFragment;
import io.knows.saturn.model.Region;
import nl.nl2312.rxcupboard.RxDatabase;
import nl.qbusict.cupboard.DatabaseCompartment;

/**
 * Created by ryun on 15-4-25.
 */
public class StartActivity extends Activity {
    @Inject
    RennClient mRennClient;

    @Inject
    DatabaseCompartment mDatabaseCompartment;
    @Inject
    RxDatabase mRxDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        inject();

        if (mRennClient.isLogin()) {
            startActivity(new Intent(this, SignupActivity.class));
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_frame, new AuthFragment())
                    .commit();
        }


    }
}
