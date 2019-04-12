package com.movesense.samples.sensorsample;

/**
 * Created by lipponep on 22.11.2017.
 */

import com.google.gson.annotations.SerializedName;

public class AccDataResponse {

    @SerializedName("Body")
    public final Body body;

    public AccDataResponse(Body body) {
        this.body = body;
    }

    public static class Body {
        @SerializedName("Timestamp")
        public final long timestamp;

        @SerializedName("ArrayAcc")
        public final Array[] array;

        @SerializedName("Headers")
        public final Headers header;

        public Body(long timestamp, Array[] array, Headers header) {
            this.timestamp = timestamp;
            this.array = array;
            this.header = header;
        }
    }

    public static class Array {
        @SerializedName("x")
        public final double x;

        @SerializedName("y")
        public final double y;

        @SerializedName("z")
        public final double z;

        public Array(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class Headers {
        @SerializedName("Param0")
        public final int param0;

        public Headers(int param0) {
            this.param0 = param0;
        }
    }
}
