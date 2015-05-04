package io.knows.saturn.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.SaturnApp;

/**
 * Created by ryun on 15-4-30.
 */
public abstract class SubmitActivity extends Activity {
    @InjectView(R.id.toolbar)
    protected Toolbar mToolbar;

    @InjectView(R.id.text_title)
    protected TextView mPageTitle;
    @InjectView(R.id.button_submit)
    protected TextView mPageSubmit;
    @InjectView(R.id.button_back)
    protected TextView mPageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        inject();

        setSupportActionBar(mToolbar);
    }

    @OnClick(R.id.button_back)
    void back() {
        finish();
    }

    @OnClick(R.id.button_submit)
    void submit() {
        if (null != mOnSubmitListener) {
            mOnSubmitListener.onSubmit();
        }
    }

    public void setPageTitle(String title) {
        mPageTitle.setText(title);
    }

    public void setOnPageSubmitListener(OnPageSubmitListener listener) {
        mOnSubmitListener = listener;
    }

    OnPageSubmitListener mOnSubmitListener;

    public interface OnPageSubmitListener {
        void onSubmit();
    }
}
