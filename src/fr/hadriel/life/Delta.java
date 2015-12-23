package fr.hadriel.life;

/**
 * Created by glathuiliere on 21/12/2015.
 *
 * This class is just a pure Tool because i never remember how to get Delta times between updates...
 * So i now have my own tool to get it, and calculate deltas xD.
 */
public class Delta {

    private static final double NS_TO_MS = 1000000D;
    private long last = System.nanoTime(); // just to avoid the first call to be a HUGE delta.

    public double getDelta() {
        long now = System.nanoTime();
        double delta = (now - last) / NS_TO_MS;
        last = now;
        return delta;
    }
}
