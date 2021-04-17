package com.example.tateti.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tateti.R;

@SuppressWarnings("deprecation")
public class CeldaView extends LinearLayout {

    private boolean mPlayed;

    public CeldaView(Context context) {
        super(context);
        init(context);
    }

    public CeldaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CeldaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.celda_view, this);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPlayed && enabled) return;
        findViewById(R.id.overlay).setVisibility(enabled ? VISIBLE : GONE);
        findViewById(R.id.overlay).setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public void setWon(boolean amX) {
        findViewById(R.id.overlay).setVisibility(GONE);
        findViewById(R.id.overlay_2).setVisibility(VISIBLE);
        findViewById(R.id.overlay_2).setEnabled(amX);

//        findViewById(R.id.overlay).setBackgroundColor(amX ? Color.RED : Color.BLUE);
    }

    public void setSymbol(boolean amX) {
        mPlayed = true;
        ((TextView) findViewById(R.id.celda_view_text)).setText(amX ? "X" : "O");
        ((TextView) findViewById(R.id.celda_view_text)).setTextColor(amX ? Color.RED : Color.BLUE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable()) return false;
        return super.onTouchEvent(event);
    }

    public enum State {
        PLAYER_1,
        PLAYER_2,
        EMPTY,
        FULL;
    }
}
