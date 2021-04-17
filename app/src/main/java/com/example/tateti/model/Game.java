package com.example.tateti.model;

import android.os.Handler;

import com.example.tateti.State;
import com.example.tateti.view.CeldaView;

import java.util.ArrayList;

public class Game implements BaseAI.AIinfo {

    public Integer tableroJugable; //-1 = ninguno, null = todos
    private Player homePlayer;
    private Player awayPlayer;

    private ArrayList<CeldaView.State> tableroStates = new ArrayList<>();
    private ArrayList<ArrayList<CeldaView.State>> celdaStates = new ArrayList<>();

    public static final int CELDAS = 9;

    private State state = State.NOT_STARTED;

    public Game(Player home, Player away) {
        homePlayer = home;
        awayPlayer = away;

        for (int i = 0; i < CELDAS; i++) {
            tableroStates.add(CeldaView.State.EMPTY);
            celdaStates.add(new ArrayList<CeldaView.State>());
            for (int j = 0; j < CELDAS; j++) {
                celdaStates.get(i).add(CeldaView.State.EMPTY);
            }
        }
    }

    public void onAttackResolved(int tablero, int celda, TurnRequestedCallback callback) {
        updateStates(tablero, celda, CeldaView.State.PLAYER_1);

        //Feo pero buen

        if (isGameWon() || isTableroFull(tableroStates)) {
            state = State.NOT_STARTED;
            tableroJugable = -1;
        } else {
            player2(callback);
        }
    }

    public void onDefense(int tablero, int celda) {
        updateStates(tablero, celda, CeldaView.State.PLAYER_2);
        if (isGameWon() || isTableroFull(tableroStates)) {
            state = State.NOT_STARTED;
            tableroJugable = -1;
        } else {
            state = State.MI_TURNO;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 300);
        }
    }

    private void updateStates(int tablero, int celda, CeldaView.State player){
        celdaStates.get(tablero).set(celda, player);

        if (isTableroFull(celdaStates.get(tablero))) tableroStates.set(tablero, CeldaView.State.FULL);
        else {

            tableroStates.set(tablero, isTableroWon(celdaStates.get(tablero)) ?
                    player : CeldaView.State.EMPTY);
            tableroJugable = tableroStates.get(celda) == CeldaView.State.EMPTY ? celda : null;
        }
    }

    public State getState() {
        return state;
    }

    public void player1() {
        state = State.MI_TURNO;
    }

    public void player2(TurnRequestedCallback callback) {
        state = State.WAITING_FOR_AWAY_PLAYER;
        awayPlayer.doTurn(callback);
    }

    public void waiting() {
        state = State.WAITING_FOR_AWAY_PLAYER;
    }

    public CeldaView.State getState(int i) {
        return tableroStates.get(i);
    }

    @Override
    public ArrayList<CeldaView.State> getTableroState(int tablero) {
        return (ArrayList<CeldaView.State>) celdaStates.get(tablero).clone();
    }

    public boolean isvalidPlay(int tablero, int celda) {
        if (tableroStates.get(tablero) != CeldaView.State.EMPTY) return false;
        if (celdaStates.get(tablero).get(celda) != CeldaView.State.EMPTY) return false;
        return tableroJugable == null || tablero == tableroJugable;
    }

    public interface TurnRequestedCallback {
        void onTurnResolved(int tablero, int celda);
    }

    public static int[][] getAdyacentes() {
        return new int[][]{
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {0, 3, 6},
                {1, 4, 7},
                {2, 5, 8},
                {0, 4, 8},
                {2, 4, 6}};
    }

    public boolean isGameWon() {
        return isTableroWon(tableroStates);
    }

    private static boolean isTableroFull(ArrayList<CeldaView.State> ts) {
        for (int j = 0; j < CELDAS; j++) {
            if (ts.get(j) == CeldaView.State.EMPTY) {
                return false;
            }
        }
        return true;
    }

    //Solo para poner después de jugar ahí
    public static boolean isTableroWon(ArrayList<CeldaView.State> ts) {
        for (int i = 0; i<getAdyacentes().length; i++) {
            if (ts.get(getAdyacentes()[i][0]) == ts.get(getAdyacentes()[i][1])
                    && ts.get(getAdyacentes()[i][0]) == ts.get(getAdyacentes()[i][2])
                    && ts.get(getAdyacentes()[i][0]) != CeldaView.State.EMPTY) {
                return true;
            }
        }
        return false;
    }
}
