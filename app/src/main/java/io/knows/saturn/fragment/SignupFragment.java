package io.knows.saturn.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennExecutor;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.GetUserParam;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.activity.RegionPickerActivity;
import io.knows.saturn.activity.SchoolPickerActivity;
import io.knows.saturn.activity.SignupActivity;
import io.knows.saturn.model.renren.RennUser;
import io.knows.saturn.service.SamuiService;
import io.knows.saturn.widget.DatePickerDialogWithMaxMinRange;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-22.
 */
public class SignupFragment extends Fragment {
    @Inject
    RennClient mRennClient;
    @Inject
    SamuiService mSamuiService;

    @InjectView(R.id.input_nickname)
    EditText mNicknameInput;

    @InjectView(R.id.input_birthday)
    EditText mBirthdayInput;

    @InjectView(R.id.input_gender)
    EditText mGenderInput;

    @InjectView(R.id.input_school)
    EditText mSchoolInput;

    @InjectView(R.id.input_hometown)
    EditText mHomeTownInput;

    static final int PAGE_SCHOOL_PICKER = 1;
    static final int PAGE_REGION_PICKER = 2;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_signup, container, false);
        inject(layout);

        loadRennProfile();

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case PAGE_SCHOOL_PICKER:
                    mSchoolInput.setText(data.getStringExtra(SignupActivity.INTENT_KEY_SCHOOL));
                    break;
                case PAGE_REGION_PICKER:
                    mHomeTownInput.setText(data.getStringExtra(SignupActivity.INTENT_KEY_REGION));
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.input_birthday)
    void birthday() {
        DialogFragment fragment = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Calendar minCalendar = Calendar.getInstance();
                minCalendar.set(1990, 0, 1);
                final Calendar maxCalendar = Calendar.getInstance();
                maxCalendar.set(maxCalendar.get(Calendar.YEAR) - 10, 11, 31);

                return new DatePickerDialogWithMaxMinRange(getActivity()
                        , (view, year, monthOfYear, dayOfMonth) -> mBirthdayInput.setText(String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth))
                        , minCalendar, maxCalendar, (Calendar) minCalendar.clone());
            }
        };
        fragment.show(getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.input_gender)
    void gender() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.gender_types, (dialog, which) -> mGenderInput.setText(getResources().getStringArray(R.array.gender_types)[which]));
        builder.create().show();
    }

    @OnClick(R.id.input_school)
    void school() {
        startActivityForResult(new Intent(getActivity(), SchoolPickerActivity.class), PAGE_SCHOOL_PICKER);
    }

    @OnClick(R.id.input_hometown)
    void hometown() {
        startActivityForResult(new Intent(getActivity(), RegionPickerActivity.class), PAGE_REGION_PICKER);
    }

    void loadRennProfile() {
        try {
            GetUserParam param = new GetUserParam();
            param.setUserId(mRennClient.getUid());
            mRennClient.getRennService().sendAsynRequest(param, new RennExecutor.CallBack() {
                @Override
                public void onSuccess(RennResponse response) {
                    Toast.makeText(getActivity(), "获取成功", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject obj = response.getResponseObject();
                        Timber.d(obj.toString());

                        RennUser user = new Gson().fromJson(obj.toString(), RennUser.class);

                        mNicknameInput.setText(user.name);
                        mBirthdayInput.setText(user.basicInformation.birthday);
                        mGenderInput.setText(user.basicInformation.sex.getText());
                        mHomeTownInput.setText(user.basicInformation.homeTown.getText());

                        RennUser.School school = user.getSchool();
                        if (null != school) {
                            mSchoolInput.setText(school.name);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                      Toast.makeText(getActivity(), "获取失败：" + errorCode + ":" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (RennException e) {
            e.printStackTrace();
        }
    }
}