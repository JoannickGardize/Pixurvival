package com.pixurvival.core.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vector2 {

    private static final NumberFormat TO_STRING_FORMAT = new DecimalFormat("0.0#");
    private float x = 0;
    private float y = 0;

    public Vector2(Vector2 v) {
        x = v.getX();
        y = v.getY();
    }

    public Vector2 copy() {
        return new Vector2(x, y);
    }

    public Vector2 set(Vector2 v) {
        x = v.getX();
        y = v.getY();
        return this;
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public static Vector2 fromEuclidean(float length, float direction) {
        return new Vector2((float) Math.cos(direction) * length, (float) Math.sin(direction) * length);
    }

    public Vector2 setFromEuclidean(float length, float direction) {
        x = (float) Math.cos(direction) * length;
        y = (float) Math.sin(direction) * length;
        return this;
    }

    public Vector2 addX(float x) {
        this.x += x;
        return this;
    }

    public Vector2 addY(float y) {
        this.y += y;
        return this;
    }

    public Vector2 add(Vector2 v) {
        x += v.getX();
        y += v.getY();
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 addEuclidean(float length, float direction) {
        x += Math.cos(direction) * length;
        y += Math.sin(direction) * length;
        return this;
    }

    public Vector2 sub(Vector2 v) {
        x -= v.getX();
        y -= v.getY();
        return this;
    }

    public Vector2 mul(float d) {
        x *= d;
        y *= d;
        return this;
    }

    public Vector2 div(float d) {
        x /= d;
        y /= d;
        return this;
    }

    public static float dot(Vector2 left, Vector2 right) {
        return (left.x * right.x) + (left.y * right.y);
    }

    public Vector2 lerp(Vector2 target, float delta) {
        x += (target.x - x) * delta;
        y += (target.y - y) * delta;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public float angle() {
        return (float) Math.atan2(y, x);
    }

    public float angleToward(Vector2 other) {
        return (float) Math.atan2(other.y - y, other.x - x);
    }

    public float angleToward(float x, float y) {
        return (float) Math.atan2(y - this.y, x - this.x);
    }

    public float distanceSquared(Vector2 v) {
        float dx = x - v.x;
        float dy = y - v.y;
        return dx * dx + dy * dy;
    }

    public float distance(Vector2 v) {
        return (float) Math.sqrt(distanceSquared(v));
    }

    public float distanceSquared(float x, float y) {
        float dx = this.x - x;
        float dy = this.y - y;
        return dx * dx + dy * dy;
    }

    public boolean epsilonEquals(Vector2 other, float epsilon) {
        return other != null && Math.abs(other.x - x) <= epsilon && Math.abs(other.y - y) <= epsilon;

    }

    public boolean insideSquare(Vector2 center, float halfLength) {
        return Math.abs(x - center.x) <= halfLength && Math.abs(y - center.y) <= halfLength;
    }

    public void write(ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
    }

    public void apply(ByteBuffer buffer) {
        x = buffer.getFloat();
        y = buffer.getFloat();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(TO_STRING_FORMAT.format(x));
        sb.append(", ");
        sb.append(TO_STRING_FORMAT.format(y));
        sb.append(")");
        return sb.toString();
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<Vector2> {

        @Override
        public void write(Kryo kryo, Output output, Vector2 object) {
            output.writeFloat(object.x);
            output.writeFloat(object.y);
        }

        @Override
        public Vector2 read(Kryo kryo, Input input, Class<Vector2> type) {
            return new Vector2(input.readFloat(), input.readFloat());
        }
    }
}
