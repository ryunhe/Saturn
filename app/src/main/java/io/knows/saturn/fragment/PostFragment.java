package io.knows.saturn.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aviary.android.feather.sdk.AviaryIntent;
import com.aviary.android.feather.sdk.internal.Constants;
import com.aviary.android.feather.sdk.internal.filters.ToolLoaderFactory;
import com.aviary.android.feather.sdk.internal.headless.utils.MegaPixels;
import com.aviary.android.feather.sdk.utils.AviaryIntentConfigurationValidator;
import com.github.pwittchen.prefser.library.Prefser;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.knows.saturn.R;
import io.knows.saturn.activity.CropperActivity;
import io.knows.saturn.activity.SubmitActivity;
import io.knows.saturn.helper.FileHelper;
import io.knows.saturn.helper.LocationManager;
import io.knows.saturn.model.Resource;
import io.knows.saturn.service.ApiService;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-22.
 */
public class PostFragment extends Fragment {
    @Inject
    UploadManager mUploadManager;
    @Inject
    ApiService mApiService;
    @Inject
    Prefser mPrefser;

    @InjectView(R.id.image_resource)
    ImageView mImage;
    @InjectView(R.id.input_content)
    EditText mContentInput;
    @InjectView(R.id.text_content_count)
    TextView mContentCountText;
    @InjectView(R.id.text_location)
    TextView mLocationText;

    Uri mResourceUri;
    Resource mResource;
    Integer mContentMaxLength;

    final static int PREVIEW_SIZE = Resource.ResourceSize.ORIGINAL.getSize();

    static final int PAGE_IMAGE_CROPPER = 5;
    static final int PAGE_IMAGE_SELECTOR = 10;
    static final int PAGE_AVIARY_EDITOR = 20;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_post, container, false);
        inject(layout);

        mContentMaxLength = getResources().getInteger(R.integer.content_max_length);
        mContentCountText.setText(mContentMaxLength.toString());
        mLocationText.setText(String.format("%s - %.3f, %.3f"
                , LocationManager.getPoi(mPrefser)
                , LocationManager.getLongitude(mPrefser)
                , LocationManager.getLatitude(mPrefser)));

        ((SubmitActivity) getActivity()).setOnPageSubmitListener(() -> {
            try {
                submit();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        // pre-load the cds service
        Intent cdsIntent = AviaryIntent.createCdsInitIntent(getActivity());
        getActivity().startService(cdsIntent);

        // verify the CreativeSDKImage configuration
        try {
            AviaryIntentConfigurationValidator.validateConfiguration(getActivity());
        } catch (Throwable e) {
            new AlertDialog.Builder(getActivity()).setMessage(e.getMessage()).show();
        }

//        start();

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case PAGE_AVIARY_EDITOR:

                    if (null != data) {
                        mResourceUri = data.getData();
                        Timber.i("Aviary result: " + mResourceUri.getPath());

                        try {
                            Bitmap bitmap = FileHelper.getBitmapWithSize(getActivity(), mResourceUri, 200, 200);
                            mImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), R.string.alert_load_image_failed, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        if (data.hasExtra(Constants.EXTRA_OUT_BITMAP_CHANGED)) {
                            boolean changed = data.getExtras().getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);

                            if (!changed) {
                                Timber.i("User did not modify the image, but just clicked on 'Done' button");
                            }
                        }
                    }

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
                    Uri input = Uri.fromFile(new File(data.getData().getPath()));
                    Uri output = Uri.fromFile(FileHelper.createTmpFile(getActivity()));
                    ToolLoaderFactory.Tools[] tools = new ToolLoaderFactory.Tools[6];
                    tools[0] = ToolLoaderFactory.Tools.EFFECTS;
                    tools[1] = ToolLoaderFactory.Tools.ENHANCE;
                    tools[2] = ToolLoaderFactory.Tools.FOCUS;
                    tools[3] = ToolLoaderFactory.Tools.DRAW;
                    tools[4] = ToolLoaderFactory.Tools.ORIENTATION;
                    tools[5] = ToolLoaderFactory.Tools.LIGHTING;

                    Timber.i("Aviary input: " + input.getPath());
                    Timber.i("Aviary output: " + output.getPath());

                    Intent i = new AviaryIntent.Builder(getActivity()).setData(input)
                            .withToolList(tools)
                            .quickLaunchTool(tools[0].name(), new Bundle())
                            .withOutput(output)
                            .withOutputFormat(Bitmap.CompressFormat.JPEG)
                            .withOutputSize(MegaPixels.Mp2)
                            .withNoExitConfirmation(true)
                            .withPreviewSize(PREVIEW_SIZE)
                            .saveWithNoChanges(true)
                            .build();
                    startActivityForResult(i, PAGE_AVIARY_EDITOR);
                    break;
            }
        } else {
            getActivity().finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.image_resource)
    void start() {
        Intent i = new Intent(getActivity(), MultiImageSelectorActivity.class);
        i.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        i.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(i, PAGE_IMAGE_SELECTOR);
    }

    void submit() throws ParseException {
        if (null != mResourceUri) {
            mApiService.getQiniuToken()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(stringResponse -> {
                        mUploadManager.put(mResourceUri.getPath(), null, stringResponse.getString(), (key, info, response) -> {
                            try {
                                mResource = new Resource(response.getString("key"));
                                Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();

                                mApiService.createMedia(mResource.identity
                                        , mContentInput.getText().toString()
                                        , LocationManager.getLatitude(mPrefser)
                                        , LocationManager.getLongitude(mPrefser))

                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(mediaEntityResponse -> {
                                            Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
                                            getActivity().setResult(Activity.RESULT_OK);
                                            getActivity().finish();
                                        });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, null);
                    });
        }
    }

    @OnTextChanged(R.id.input_content)
    void count(CharSequence s, int start, int before, int count) {
        mContentCountText.setText(Integer.toString(mContentMaxLength - mContentInput.getText().length()));
    }
}
