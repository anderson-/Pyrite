/*
    Copyright (c) 2013, 2014 pachacamac

    This file is part of jg3d.

    jg3d is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jg3d is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/

package s3f.pyrite.core.fdgfa.fdgs;

public class TpsCounter {

    long[] times;
    long lastTick;
    int ptr;
    long calls;

    public TpsCounter(int bufferSize) {
        times = new long[bufferSize];
        ptr = 0;
        calls = 0;
        lastTick = System.currentTimeMillis();
    }

    public long tick() {
        calls++;
        ptr = (ptr + 1) % times.length;
        times[ptr] = System.currentTimeMillis() - lastTick;
        lastTick = System.currentTimeMillis();
        return calls;
    }

    public float get() {
        int timeSum = 0;
        for (int i = 0; i < times.length; i++) {
            timeSum += times[i];
        }
        return (1.0f / ((float) timeSum / (float) times.length)) * 1000.0f;
    }

    @Override
    public String toString() {
        return Double.toString(Math.ceil(get() * 100) / 100);
    }
}
