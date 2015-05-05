package io.knows.saturn.model;

import android.support.annotation.Nullable;

import com.github.pwittchen.prefser.library.Prefser;

import io.knows.saturn.helper.StorageWrapper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by ryun on 15-4-30.
 */
public class Authenticator {
    User mUser = new User();
    final PublishSubject<Authenticator> mSubject = PublishSubject.create();
    final StorageWrapper mStorage;
    final Prefser mPrefser;
    static Authenticator mInstance;

    private Authenticator(StorageWrapper storage, Prefser prefser) {
        mStorage = storage;
        mPrefser = prefser;

        if (null != getUserId()) {
            mStorage.load(User.class, getUserId()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setUser);
        }
    }

    public void saveUser(User user) {
        user.save(mStorage);
        mPrefser.put(Preferences.AUTHENTICATOR_USER_ID.toString(), user.id);

        setUser(user);
    }

    public void setUser(User user) {
        mUser = user;
        mSubject.onNext(this);
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

    public Observable<Authenticator> getObservable() {
        return mSubject.asObservable().startWith(this);
    }

    public static Authenticator getInstance(StorageWrapper storage, Prefser prefser) {
        if (mInstance == null) {
            mInstance = new Authenticator(storage, prefser);
        }
        return mInstance;
    }
}
