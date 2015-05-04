package io.knows.saturn.model;

import android.content.SharedPreferences;

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
    final SharedPreferences mPreferences;
    static Authenticator mInstance;

    private Authenticator(StorageWrapper storage, SharedPreferences preferences) {
        mStorage = storage;
        mPreferences = preferences;

        if (null != getUserId()) {
            mStorage.load(User.class, getUserId()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setUser);
        }
    }

    public void saveUser(User user) {
        user.save(mStorage);
        mPreferences.edit().putString(Preferences.AUTHENTICATOR_USER_ID.toString(), user.id).apply();

        setUser(user);
    }

    public void setUser(User user) {
        mUser = user;
        mSubject.onNext(this);
    }

    public User getUser() {
        return mUser;
    }

    public String getUserId() {
        return mPreferences.getString(Preferences.AUTHENTICATOR_USER_ID.toString(), null);
    }

    public Boolean isLoggedIn() {
        return null != getUserId();
    }

    public Observable<Authenticator> getObservable() {
        return mSubject.asObservable().startWith(this);
    }

    public static Authenticator getInstance(StorageWrapper storage, SharedPreferences preferences) {
        if (mInstance == null) {
            mInstance = new Authenticator(storage, preferences);
        }
        return mInstance;
    }
}
