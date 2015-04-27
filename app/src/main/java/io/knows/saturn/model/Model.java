package io.knows.saturn.model;

import java.lang.reflect.Type;
import java.util.List;

import nl.nl2312.rxcupboard.RxDatabase;
import nl.qbusict.cupboard.annotation.Index;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-20.
 */
public class Model {
    public Long _id; // for cupboard
    @Index(unique = true)
    public String id;

    public void save(RxDatabase db) {
        save(this, db);
    }

    public static <T extends Model> void save(T entity, RxDatabase db) {
        load(entity.getClass(), db, entity.id).subscribe(new Observer<Model>() {
            @Override
            public void onCompleted() {
                db.put(entity);
            }

            @Override
            public void onError(Throwable e) {
                throw new OnErrorNotImplementedException(e);
            }

            @Override
            public void onNext(Model model) {
                if (null != model._id) { // update
                    entity._id = model._id;
                }
            }
        });
    }

    public static Observable<? extends Model> load(Class<? extends Model> entityClass, RxDatabase db, String id) {
        return find(entityClass, db, "id = ?", id).take(1);
    }

    public static Observable<? extends Model> find(Class<? extends Model> entityClass, RxDatabase db, String selection, String... args) {
        return db.query(entityClass, selection, args);
    }
}
