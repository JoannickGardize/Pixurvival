package com.pixurvival.contentPackEditor.util;

import lombok.Getter;

import java.util.function.Consumer;

public class Array2D<T> {

    private @Getter Object[] data;
    private @Getter int width;
    private @Getter int height;

    public Array2D(int width, int height) {
        this.width = width;
        this.height = height;
        data = new Object[width * height];
    }

    public void set(int x, int y, T value) {
        data[y * width + x] = value;
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        return (T) data[y * width + x];
    }

    public void resize(int width, int height) {
        Object[] newData = new Object[width * height];
        int minWidth = Math.min(this.width, width);
        int minHeight = Math.min(this.height, height);
        for (int x = 0; x < minWidth; x++) {
            for (int y = 0; y < minHeight; y++) {
                newData[y * width + x] = get(x, y);
            }
        }
        this.data = newData;
        this.width = width;
        this.height = height;
    }

    public void forEach(Consumer<T> action) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                action.accept(get(x, y));
            }
        }
    }
}
