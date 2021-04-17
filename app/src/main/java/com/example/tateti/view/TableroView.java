package com.example.tateti.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.tateti.R;

import java.util.ArrayList;

public class TableroView extends LinearLayout {

    private int[] celdas = {R.id.celda_11, R.id.celda_12, R.id.celda_13, R.id.celda_21, R.id.celda_22, R.id.celda_23, R.id.celda_31, R.id.celda_32, R.id.celda_33};
    private ArrayList<CeldaView> celdaviews = new ArrayList<>();

    public TableroView(Context context) {
        super(context);
        init(context);
    }

    public TableroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TableroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.tablero_view, this);
    }

    public void setListeners(final OnCeldaClickeadaCallback onCeldaClickeadaCallback) {
        for (int i = 0; i < celdas.length; i++) {
            celdaviews.add((CeldaView) findViewById(celdas[i]));
            final int finalI = i;
            celdaviews.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCeldaClickeadaCallback.onCeldaClickeada(finalI);
                }
            });
        }
    }

    @Override
    public void setEnabled(boolean selected) {
        for (CeldaView celdaview : celdaviews) {
            celdaview.setEnabled(selected);
        }
        super.setEnabled(selected);
    }

    @Override
    public void setClickable(boolean selected) {
        for (CeldaView celdaview : celdaviews) {
            celdaview.setClickable(selected);
        }
        super.setClickable(selected);
    }


    public void setWon(boolean amX) {
        for (CeldaView celdaview : celdaviews) {
            celdaview.setWon(amX);
        }
    }

    public void play(int celda, boolean amX) {
        celdaviews.get(celda).setSymbol(amX);
    }

    public interface OnCeldaClickeadaCallback {
        void onCeldaClickeada(int celda);
    }

}
