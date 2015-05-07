package io.knows.saturn.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.helper.FileHelper;
import io.knows.saturn.model.Resource;

/**
 * Created by ryun on 15-4-22.
 */
public class CropperFragment extends Fragment {
    final static int MAX_SIZE = Resource.ResourceSize.ORIGINAL.getSize();

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
            Bitmap bitmap = FileHelper.getBitmapWithSize(getActivity(), resource, MAX_SIZE, MAX_SIZE);
            mCropperImage.setImageBitmap(bitmap);
            mCropperImage.setFixedAspectRatio(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
