package com.pixurvival.core.util;

import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@NoArgsConstructor
public class DebugVector2 extends Vector2 {

    public DebugVector2(Vector2 v) {
        super(v);
    }

    public DebugVector2(float x, float y) {
        super(x, y);
    }

    @Override
    public Vector2 set(Vector2 v) {
        System.out.println("set(" + v + ")");
        return super.set(v);
    }

    @Override
    public Vector2 set(float x, float y) {
        System.out.println("set(" + x + ", " + y + ")");
        return super.set(x, y);
    }

    @Override
    public Vector2 setFromEuclidean(float length, float direction) {
        System.out.println("setFromEuclidean(" + length + ", " + direction + ")");
        return super.setFromEuclidean(length, direction);
    }

    @Override
    public Vector2 addX(float x) {
        System.out.println("addX(" + x + ")");
        return super.addX(x);
    }

    @Override
    public Vector2 addY(float y) {
        System.out.println("addY(" + y + ")");
        return super.addY(y);
    }

    @Override
    public Vector2 add(Vector2 v) {
        System.out.println("add(" + v + ")");
        return super.add(v);
    }

    @Override
    public Vector2 add(float x, float y) {
        System.out.println("add(" + x + ", " + y + ")");
        return super.add(x, y);
    }

    @Override
    public Vector2 addEuclidean(float length, float direction) {
        System.out.println("addEuclidean(" + length + ", " + direction + ")");
        return super.addEuclidean(length, direction);
    }

    @Override
    public Vector2 sub(Vector2 v) {
        System.out.println("sub(" + v + ")");
        return super.sub(v);
    }

    @Override
    public Vector2 mul(float d) {
        System.out.println("mul(" + d + ")");
        return super.mul(d);
    }

    @Override
    public Vector2 div(float d) {
        System.out.println("div(" + d + ")");
        return super.div(d);
    }

    @Override
    public Vector2 lerp(Vector2 target, float delta) {
        System.out.println("lerp(" + target + ", " + delta + ")");
        return super.lerp(target, delta);
    }

    @Override
    public void apply(ByteBuffer buffer) {
        System.out.println("apply(" + buffer + ")");
        super.apply(buffer);
    }
}
