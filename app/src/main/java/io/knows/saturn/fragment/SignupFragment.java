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

import com.renn.rennsdk.RennClient;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.activity.SchoolPickerActivity;
import io.knows.saturn.activity.SignupActivity;
import io.knows.saturn.service.SamuiService;
import io.knows.saturn.widget.DatePickerDialogWithMaxMinRange;

/**
 * Created by ryun on 15-4-22.
 */
public class SignupFragment extends Fragment {
    @Inject
    RennClient mRennClient;
    @Inject
    SamuiService mSamuiService;

    @InjectView(R.id.input_birthday)
    EditText mBirthdayInput;

    @InjectView(R.id.input_gender)
    EditText mGenderInput;

    @InjectView(R.id.input_school)
    EditText mSchoolInput;

    @InjectView(R.id.input_hometown)
    EditText mHomeTownInput;

    static final int PAGE_SCHOOL_PICKER = 1;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_signup, container, false);
        inject(layout);

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case PAGE_SCHOOL_PICKER:
                    mSchoolInput.setText(data.getStringExtra(SignupActivity.INTENT_KEY_SCHOOL));
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
    }
}
