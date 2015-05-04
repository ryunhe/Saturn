package io.knows.saturn.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;

/**
 * Created by ryun on 15-4-30.
 */
public abstract class MenuActivity extends Activity {
    @InjectView(R.id.toolbar)
    protected Toolbar mToolbar;

    @InjectView(R.id.text_title)
    protected TextView mPageTitle;
    @InjectView(R.id.button_back)
    protected TextView mPageBack;
    @InjectView(R.id.button_menu)
    protected TextView mPageMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        inject();

        setSupportActionBar(mToolbar);
    }

    @OnClick(R.id.button_back)
    void back() {
        finish();
    }

    @OnClick(R.id.button_menu)
    void menu() {

    }

}
