package com.pixurvival.core.map.generator;

import com.pixurvival.core.util.IntUrn;
import com.pixurvival.core.util.MathUtils;
import lombok.AllArgsConstructor;

import java.util.Random;

/*
 * A speed-improved simplex noise algorithm for 2D, 3D and 4D in Java.
 *
 * Based on example code by Stefan Gustavson (stegu@itn.liu.se).
 * Optimisations by Peter Eastman (peastman@drizzle.stanford.edu).
 * Better rank ordering method by Stefan Gustavson in 2012.
 *
 * This could be speeded up even further, but it's useful as it is.
 *
 * Version 2012-03-09
 *
 * This code was placed in the public domain by its original author,
 * Stefan Gustavson. You may use it as you see fit, but
 * attribution is appreciated.
 *
 */

public class SimplexNoiseOctave { // Simplex noise in 2D, 3D and 4D

    private static Grad[] grad3 = {new Grad(1, 1), new Grad(-1, 1), new Grad(1, -1), new Grad(-1, -1), new Grad(1, 0), new Grad(-1, 0), new Grad(1, 0), new Grad(-1, 0), new Grad(0, 1),
            new Grad(0, -1), new Grad(0, 1), new Grad(0, -1)};

    // To remove the need for index wrapping, float the permutation table
    // length
    private short[] perm = new short[512];
    private short[] permMod12 = new short[512];

    public SimplexNoiseOctave(long seed) {

        // the random for the swaps
        Random rand = new Random(seed);

        IntUrn urn = new IntUrn(256);
        short[] arraySeed = new short[256];
        for (int i = 0; i < 256; i++) {
            arraySeed[i] = (short) urn.draw(rand);
        }
        System.arraycopy(arraySeed, 0, perm, 0, 256);
        System.arraycopy(arraySeed, 0, perm, 256, 256);

        for (int i = 0; i < 256; i++) {
            permMod12[i] = (short) (perm[i] % 12);
        }
        System.arraycopy(permMod12, 0, permMod12, 256, 256);
    }

    // Skewing and unskewing factors for 2, 3, and 4 dimensions
    private static final float F2 = 0.5f * ((float) Math.sqrt(3.0) - 1.0f);
    private static final float G2 = (3.0f - (float) Math.sqrt(3.0)) / 6.0f;

    private static float dot(Grad g, float x, float y) {
        return g.x * x + g.y * y;
    }

    // 2D simplex noise
    public float noise(float xin, float yin) {
        float n0, n1, n2; // Noise contributions from the three corners
        // Skew the input space to determine which simplex cell we're in
        float s = (xin + yin) * F2; // Hairy factor for 2D
        int i = MathUtils.floor(xin + s);
        int j = MathUtils.floor(yin + s);
        float t = (i + j) * G2;
        float X0 = i - t; // Unskew the cell origin back to (x,y) space
        float Y0 = j - t;
        float x0 = xin - X0; // The x,y distances from the cell origin
        float y0 = yin - Y0;
        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.
        int i1, j1; // Offsets for second (middle) corner of simplex in (i,j)
        // coords
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } // lower triangle, XY order: (0,0)->(1,0)->(1,1)
        else {
            i1 = 0;
            j1 = 1;
        } // upper triangle, YX order: (0,0)->(0,1)->(1,1)
        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6
        float x1 = x0 - i1 + G2; // Offsets for middle corner in (x,y) unskewed
        // coords
        float y1 = y0 - j1 + G2;
        float x2 = x0 - 1.0f + 2.0f * G2; // Offsets for last corner in (x,y)
        // unskewed coords
        float y2 = y0 - 1.0f + 2.0f * G2;
        // Work out the hashed gradient indices of the three simplex corners
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = permMod12[ii + perm[jj]];
        int gi1 = permMod12[ii + i1 + perm[jj + j1]];
        int gi2 = permMod12[ii + 1 + perm[jj + 1]];
        // Calculate the contribution from the three corners
        float t0 = 0.5f - x0 * x0 - y0 * y0;
        if (t0 < 0) {
            n0 = 0;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0); // (x,y) of grad3 used for
            // 2D gradient
        }
        float t1 = 0.5f - x1 * x1 - y1 * y1;
        if (t1 < 0) {
            n1 = 0;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1);
        }
        float t2 = 0.5f - x2 * x2 - y2 * y2;
        if (t2 < 0) {
            n2 = 0;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2);
        }
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0f * (n0 + n1 + n2);
    }

    // Inner class to speed upp gradient computations
    // (array access is a lot slower than member access)
    @AllArgsConstructor
    private static class Grad {
        float x;
        float y;
    }

}