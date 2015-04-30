package io.knows.saturn.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.activity.CropperActivity;
import io.knows.saturn.activity.SignupActivity;
import io.knows.saturn.helper.FileHelper;

/**
 * Created by ryun on 15-4-22.
 */
public class CropperFragment extends Fragment {
    @InjectView(R.id.image_cropper)
    CropImageView mCropperImage;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_cropper, container, false);
        inject(layout);

        Uri resource = getActivity().getIntent().getData();
        try {
            Bitmap bitmap = FileHelper.getBitmapFromUriWithSize(getActivity(), resource, 1000);
            mCropperImage.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mCropperImage.setFixedAspectRatio(true);

        return layout;
    }

    @OnClick(R.id.button_confirm)
    void crop() {
        File file = FileHelper.createTmpFile(getActivity());
        FileHelper.dumpBitmapToFile(mCropperImage.getCroppedImage(), file);

        Intent i = new Intent();
        i.setData(Uri.fromFile(file));
        getActivity().setResult(Activity.RESULT_OK, i);
        getActivity().finish();
    }

    @OnClick(R.id.button_cancel)
    void back() {
        getActivity().finish();
    }
}
