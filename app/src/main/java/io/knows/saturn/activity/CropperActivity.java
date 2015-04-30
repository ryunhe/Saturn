package io.knows.saturn.activity;

import android.content.Intent;
import android.os.Bundle;

import com.renn.rennsdk.RennClient;

import javax.inject.Inject;

import io.knows.saturn.R;
import io.knows.saturn.fragment.AuthFragment;
import io.knows.saturn.fragment.CropperFragment;
import nl.nl2312.rxcupboard.RxDatabase;
import nl.qbusict.cupboard.DatabaseCompartment;

/**
 * Created by ryun on 15-4-25.
 */
public class CropperActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, new CropperFragment())
                .commit();
    }
}
