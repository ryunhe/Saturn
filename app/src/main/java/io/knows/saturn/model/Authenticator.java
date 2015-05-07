package io.knows.saturn.model;

import android.support.annotation.Nullable;

import com.github.pwittchen.prefser.library.Prefser;

import io.knows.saturn.helper.RxBus;
import io.knows.saturn.helper.StorageWrapper;
import io.knows.saturn.response.AuthResponse;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by ryun on 15-4-30.
 */
public class Authenticator {
    final StorageWrapper mStorage;
    final Prefser mPrefser;
    final RxBus<Authenticator> mBus;
    static Authenticator mInstance;

    User mUser = new User();

    private Authenticator(StorageWrapper storage, Prefser prefser) {
        mStorage = storage;
        mPrefser = prefser;
        mBus = new RxBus<>();

        if (null != getUserId()) {
            mStorage.load(User.class, getUserId()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::putUser);
        }
    }

    public void saveUser(User user) {
        user.save(mStorage);
        mPrefser.put(Preferences.AUTHENTICATOR_USER_ID.toString(), user.id);

        putUser(user);
    }

    public void putUser(User user) {
        mUser = user;
        mBus.send(this); // PRODUCING
    }

    public User getUser() {
        return mUser;
    }

    @Nullable
    public String getUserId() {
        return mPrefser.get(Preferences.AUTHENTICATOR_USER_ID.toString(), String.class, null);
    }

    public Boolean isLoggedIn() {
        return null != getUserId();
    }

    public Observable<Authenticator> toObservable() {
        return mBus.toObservable();
    }

    public static Authenticator getInstance(StorageWrapper storage, Prefser prefser) {
        if (mInstance == null) {
            mInstance = new Authenticator(storage, prefser);
        }
        return mInstance;
    }
}
