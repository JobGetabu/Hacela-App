package com.job.hacelaapp.common;

import java.util.Iterator;

/**
 * Created by Job on Saturday : 7/7/2018.
 */
public class SOList<Type> implements Iterable<Type> {

    private Type[] arrayList;
    private int currentSize;

    public SOList(Type[] newArray) {
        this.arrayList = newArray;
        this.currentSize = arrayList.length;
    }

    @Override
    public Iterator<Type> iterator() {
        Iterator<Type> it = new Iterator<Type>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < currentSize && arrayList[currentIndex] != null;
            }

            @Override
            public Type next() {
                return arrayList[currentIndex++];
            }



            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }
}
