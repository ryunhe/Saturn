package io.knows.saturn.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qiniu.android.storage.UploadManager;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennExecutor;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.GetUserParam;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.activity.CropperActivity;
import io.knows.saturn.activity.RegionPickerActivity;
import io.knows.saturn.activity.SchoolPickerActivity;
import io.knows.saturn.activity.SignupActivity;
import io.knows.saturn.activity.SubmitActivity;
import io.knows.saturn.helper.FileHelper;
import io.knows.saturn.model.Authenticator;
import io.knows.saturn.model.Resource;
import io.knows.saturn.model.User;
import io.knows.saturn.model.renren.RennUser;
import io.knows.saturn.service.SamuiService;
import io.knows.saturn.widget.DatePickerDialogWithMaxMinRange;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-22.
 */
public class SignupFragment extends Fragment {
    @Inject
    Picasso mPicasso;
    @Inject
    RennClient mRennClient;
    @Inject
    UploadManager mUploadManager;
    @Inject
    SamuiService mSamuiService;
    @Inject
    Authenticator mAuthenticator;

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

    @InjectView(R.id.image_avatar)
    ImageView mAvatarImage;

    Resource mAvatarResource;

    static final int PAGE_SCHOOL_PICKER = 1;
    static final int PAGE_REGION_PICKER = 2;
    static final int PAGE_IMAGE_CROPPER = 5;
    static final int PAGE_IMAGE_SELECTOR = 10;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_signup, container, false);
        inject(layout);

        loadRennProfile();

        ((SubmitActivity) getActivity()).setOnPageSubmitListener(new SubmitActivity.OnPageSubmitListener() {
            @Override
            public void onSubmit() {
                try {
                    submit();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

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
                case PAGE_IMAGE_SELECTOR:
                    List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (path.size() > 0) {
                        Intent i = new Intent(getActivity(), CropperActivity.class);
                        i.setData(Uri.fromFile(new File(path.get(0))));
                        startActivityForResult(i, PAGE_IMAGE_CROPPER);
                    }
                    break;
                case PAGE_IMAGE_CROPPER:
                    updateAvatarResource(data.getData().getPath());
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
                final Calendar minCalendar = Calendar.getInstance(Locale.CHINA);
                minCalendar.set(1990, 0, 1);
                final Calendar maxCalendar = Calendar.getInstance(Locale.CHINA);
                maxCalendar.set(maxCalendar.get(Calendar.YEAR) - 10, 11, 31);

                return new DatePickerDialogWithMaxMinRange(getActivity()
                        , (view, year, monthOfYear, dayOfMonth) -> mBirthdayInput.setText(String.format("%s年%s月%s日", year, monthOfYear + 1, dayOfMonth))
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

    @OnClick(R.id.button_add_avatar)
    void avatar() {
        Intent i = new Intent(getActivity(), MultiImageSelectorActivity.class);
        i.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        i.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(i, PAGE_IMAGE_SELECTOR);
    }

    void updateAvatarResource(final String filePath) {
        mSamuiService.getQiniuToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringResponse -> {
                    mUploadManager.put(filePath, null, stringResponse.getString(), (key, info, response) -> {
                        try {
                            Timber.d(response.optString("key"));
                            mAvatarResource = new Resource(response.getString("key"));
                            mPicasso.load(mAvatarResource.getUrl(Resource.ResourceSize.THUMBNAIL))
                                    .into(mAvatarImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, null);
                });
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
                        mBirthdayInput.setText(user.getFormatBirthday(new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)));
                        mGenderInput.setText(user.basicInformation.sex.getText());
                        mHomeTownInput.setText(user.basicInformation.homeTown.getText());

                        RennUser.School school = user.getSchool();
                        if (null != school) {
                            mSchoolInput.setText(school.name);
                        }

                        RennUser.Image image = user.getAvatar(RennUser.ImageSize.LARGE);
                        if (null != image) {
                            mPicasso.load(image.url).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    File file = FileHelper.createTmpFile(getActivity());
                                    FileHelper.dumpBitmapToFile(bitmap, file);
                                    updateAvatarResource(file.getPath());
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
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

    void submit() throws ParseException {
        String nickname = mNicknameInput.getEditableText().toString();
        String school = mSchoolInput.getEditableText().toString();
        String hometown = mHomeTownInput.getEditableText().toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        int birthday = (int) Math.floor(format.parse(mBirthdayInput.getEditableText().toString()).getTime() / 1000);

        User.Gender gender = User.Gender.MALE.getText().equals(mGenderInput.getEditableText().toString())
                ? User.Gender.MALE
                : User.Gender.FEMALE;

        if (null != mAvatarResource) {
            mSamuiService.createMedia(mAvatarResource.key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mediaEntityResponse -> {
                        Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                        mSamuiService.updateProfile(nickname, gender.getCode(), birthday, hometown, school, null, null)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(userEntityResponse -> {
                                    Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
                                    mAuthenticator.saveUser(userEntityResponse.getEntity());
                                    getActivity().setResult(Activity.RESULT_OK);
                                    getActivity().finish();
                                });
                    });
        }
    }
}
