package io.knows.saturn.model;

import android.content.SharedPreferences;

import nl.nl2312.rxcupboard.RxDatabase;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by ryun on 15-4-30.
 */
public class Authenticator {
    User mUser = new User();
    final PublishSubject<Authenticator> mSubject = PublishSubject.create();
    final RxDatabase mDatabase;
    final SharedPreferences mPreferences;
    static Authenticator mInstance;

    private Authenticator(RxDatabase database, SharedPreferences preferences) {
        mDatabase = database;
        mPreferences = preferences;

        String userId = mPreferences.getString(Preferences.AUTHENTICATOR_USER_ID.toString(), null);

        if (null != userId) {
            mDatabase.query(User.class, "id = ?", userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(user -> {
                        mUser = user;
                        mSubject.onNext(this);
                    });
        }
    }

    public void saveUser(User user) {
        user.save(mDatabase);
        mPreferences.edit().putString(Preferences.AUTHENTICATOR_USER_ID.toString(), user.id).apply();

        mUser = user;
        mSubject.onNext(this);
    }

    public User getUser() {
        return mUser;
    }

    public Boolean isLoggedIn() {
        return null != mUser.id;
    }

    public Observable<Authenticator> getObservable() {
        return mSubject.asObservable().startWith(this);
    }

    public static Authenticator getInstance(RxDatabase database, SharedPreferences preferences) {
        if (mInstance == null) {
            mInstance = new Authenticator(database, preferences);
        }
        return mInstance;
    }
}
