package com.movesense.samples.sensorsample;

public class StatusVar {
    private int boo = -1;
    private ChangeListener listener;

    public int isBoo() {
        return boo;
    }

    public void setBoo(int boo) {
        this.boo = boo;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
