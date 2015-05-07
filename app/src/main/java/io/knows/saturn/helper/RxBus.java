package io.knows.saturn.helper;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by ryun on 15-5-6.
 */
public class RxBus<T> {

    private final Subject<T, T> _bus = new SerializedSubject<>(PublishSubject.create());

    public void send(T o) {
        _bus.onNext(o);
    }

    public Observable<T> toObservable() {
        return _bus.asObservable();
    }
}