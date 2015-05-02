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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        inject();

        if (mRennClient.isLogin()) {
            start();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_frame, new AuthFragment())
                    .commit();
        }
    }

    public void start() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
