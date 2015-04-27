package io.knows.saturn.model;

import nl.nl2312.rxcupboard.RxDatabase;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-20.
 */
public class Media extends Model {
    public Resource resource;
    public User user;
    public String userId;

    public class Resource {
        public String standard;
        public String medium;
        public String thumbnail;
    }

    public void save(RxDatabase db) {
        userId = user.id;
        super.save(db);
    }


}
