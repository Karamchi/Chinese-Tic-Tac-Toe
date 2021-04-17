package com.example.tateti.model;

import com.example.tateti.view.CeldaView;

import java.util.ArrayList;

public abstract class BaseAI extends Player {

    protected BaseAI() {
    }

    public interface AIinfo {
        boolean isvalidPlay(int tablero, int celda);
        ArrayList<CeldaView.State> getTableroState(int tablero);
    }
}
