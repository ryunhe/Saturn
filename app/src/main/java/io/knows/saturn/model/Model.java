package io.knows.saturn.model;

import java.lang.reflect.Type;

import nl.nl2312.rxcupboard.RxDatabase;
import nl.qbusict.cupboard.annotation.Index;
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
        load(entity.getClass(), db, entity.id, new Observer<T>() {
            @Override
            public void onCompleted() {
                Timber.d(entity.getClass().getSimpleName() + " - save completed");
                db.put(entity);
            }

            @Override
            public void onError(Throwable e) {
                throw new OnErrorNotImplementedException(e);
            }

            @Override
            public void onNext(T model) {
                if (null != model._id) { // update
                    entity._id = model._id;
                }
            }
        });
    }

    public static <T extends Model> void load(Class<? extends Model> entityClass, RxDatabase db, String id, Observer<T> observer) {
        db.query(entityClass, "id = ?", id).take(1).subscribe(new Observer<Model>() {
            @Override
            public void onCompleted() {
                Timber.d(entityClass.getSimpleName() + " - load completed");
                observer.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override
            public void onNext(Model model) {
                observer.onNext((T) model);
            }
        });
    }
}
