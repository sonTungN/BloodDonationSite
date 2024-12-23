package com.sontung.blood.callback;

import java.util.List;

public interface FirebaseCallback<T> {
    void onSuccess(List<T> t);
    void onSuccess(T t);
    
    void onFailure(List<T> t);
    void onFailure(T t);
}