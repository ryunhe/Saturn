package io.knows.saturn.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.activity.ProfileActivity;
import io.knows.saturn.helper.StorageWrapper;
import io.knows.saturn.model.Authenticator;
import io.knows.saturn.model.Media;
import io.knows.saturn.model.Resource;
import io.knows.saturn.model.User;
import io.knows.saturn.service.SamuiService;
import io.knows.saturn.widget.RoundedBackgroundSpan;
import io.knows.saturn.widget.TagView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ryun on 15-4-22.
 */
public class ProfileFragment extends Fragment {
    @Inject
    Picasso mPicasso;
    @Inject
    StorageWrapper mStorageWrapper;
    @Inject
    SamuiService mSamuiService;
    @Inject
    Authenticator mAuthenticator;

    @InjectView(R.id.viewpager_resource)
    ViewPager mPager;
    @InjectView(R.id.indicator_resource)
    CirclePageIndicator mIndicator;
    @InjectView(R.id.text_primary)
    TextView mPrimaryText;
    @InjectView(R.id.text_secondary)
    TextView mSecondaryText;
    @InjectView(R.id.group_stats)
    View mStatsGroup;

    @InjectView(R.id.tags_book)
    TagView mBookTags;
    @InjectView(R.id.tags_music)
    TagView mMusicTags;
    @InjectView(R.id.tags_movie)
    TagView mMovieTags;

    ResourcePagerAdapter mPagerAdapter;
    String mUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPagerAdapter = new ResourcePagerAdapter(getFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_profile, container, false);
        inject(layout);

        mPager.setAdapter(mPagerAdapter);
        mStatsGroup.setVisibility(View.GONE);

        if (getActivity().getIntent().hasExtra(ProfileActivity.INTENT_KEY_USER)) {
            mUserId = getActivity().getIntent().getStringExtra(ProfileActivity.INTENT_KEY_USER);
        } else {
            mUserId = mAuthenticator.getUserId();
        }

        if (null != mUserId) {
            mStorageWrapper.load(User.class, mUserId).subscribe(user -> {

                ((ProfileActivity) getActivity()).setPageTitle(user.nickname);

                mPrimaryText.setText(String.format("%s, %d", user.nickname, user.age));
                mSecondaryText.setText(String.format("%s, %s", user.school, user.hometown[user.hometown.length - 1]));

                mPagerAdapter.addData(user.cover.getUrl(Resource.ResourceSize.STANDARD));
                mPagerAdapter.notifyDataSetChanged();

                if (user.counts.media > 1) {
                    mSamuiService.getUserRecentMedia(user.id, user.counts.media)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(mediaListResponse -> {
                                for (Media media : mediaListResponse.getResult()) {
                                    if (!user.cover.identity.equals(media.resource.identity)) {
                                        mPagerAdapter.addData(media.resource.getUrl(Resource.ResourceSize.STANDARD));
                                    }
                                }
                                mPagerAdapter.notifyDataSetChanged();
                                mIndicator.setViewPager(mPager);
                            });
                }

                if (null != user.likes) {
                    int tagColor = getResources().getColor(R.color.gray_light);
                    HashMap<User.LikeCategory, LinkedList<TagView.Tag>> tags = new HashMap<>();
                    tags.put(User.LikeCategory.BOOK, new LinkedList<>());
                    tags.put(User.LikeCategory.MUSIC, new LinkedList<>());
                    tags.put(User.LikeCategory.MOVIE, new LinkedList<>());
                    for (User.Like like : user.likes) {
                        tags.get(like.category).add(new TagView.Tag(like.name, tagColor));
                    }
                    mBookTags.setTags(tags.get(User.LikeCategory.BOOK), " ");
                    mMusicTags.setTags(tags.get(User.LikeCategory.MUSIC), " ");
                    mMovieTags.setTags(tags.get(User.LikeCategory.MOVIE), " ");
                }
            });
        }

        return layout;
    }

    class ResourcePagerAdapter extends FragmentPagerAdapter {
        protected List<String> dataList = new ArrayList<>();

        public ResourcePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addData(String resource) {
            dataList.add(resource);
        }

        @Override
        public Fragment getItem(int position) {
            ResourcePagerFragment fragment = new ResourcePagerFragment();
            fragment.resource = dataList.get(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        class ResourcePagerFragment extends Fragment {
            private static final String KEY_RESOURCE = "ResourcePagerFragment:Resource";

            @InjectView(R.id.image_resource)
            ImageView resourceView;

            String resource;

            @OnClick(R.id.image_resource)
            public void dismiss() {
                getActivity().finish();
            }

            @Override
            public View onCreateView(LayoutInflater inflater,
                                     @Nullable ViewGroup container,
                                     @Nullable Bundle savedInstanceState) {

                View layout = inflater.inflate(R.layout.fragment_pager_resource, container, false);
                ButterKnife.inject(this, layout);

                mPicasso.load(resource).into(resourceView);

                return layout;
            }

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_RESOURCE)) {
                    resource = savedInstanceState.getString(KEY_RESOURCE);
                }
            }

            @Override
            public void onSaveInstanceState(Bundle outState) {
                super.onSaveInstanceState(outState);
                outState.putString(KEY_RESOURCE, resource);
            }
        }
    }
}
