package io.knows.saturn.helper;

import io.knows.saturn.model.Model;
import nl.nl2312.rxcupboard.RxDatabase;
import rx.Observable;
import rx.Observer;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by ryun on 15-5-4.
 */
public class StorageWrapper {
    RxDatabase mDatabase;

    public StorageWrapper(RxDatabase database) {
        mDatabase = database;
    }

    public <T extends Model> void save(T entity) {
        load(entity.getClass(), entity.id).subscribe(new Observer<Model>() {
            @Override
            public void onCompleted() {
                mDatabase.put(entity);
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

    public <T extends Model> Observable<T> load(Class<T> entityClass, String id) {
        return find(entityClass, "id = ?", id);
    }

    public <T extends Model> Observable<T> find(Class<T> entityClass, String selection, String... args) {
        return mDatabase.query(entityClass, selection, args);
    }
}
