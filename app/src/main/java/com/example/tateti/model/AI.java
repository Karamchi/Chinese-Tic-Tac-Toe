package com.example.tateti.model;

import android.os.Handler;

import com.example.tateti.view.CeldaView;

import java.util.ArrayList;
import java.util.Random;

public class AI extends BaseAI {

    private AIinfo mGame;

    @Override
    public void doTurn(final Game.TurnRequestedCallback callback) {
        ArrayList<int[]> posibles = new ArrayList<>();
        for (int i = 0; i < Game.CELDAS; i++) {
            for (int j = 0; j< Game.CELDAS; j++) {
                if (mGame.isvalidPlay(i, j)) {
                    posibles.add(new int[]{i, j});
                }
            }
        }
        if (posibles.size() == 0) return;

        final int[] jugada = buscarJugadas(posibles);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTurnResolved(jugada[0], jugada[1]);
            }
        }, 400);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                for (int i = 0; i < mOpponentCards.size(); i++) {
//                    if (mOpponentCards.get(i).size() != 0) {
//                        topsEl.add(new Pair<>(mOpponentCards.get(i).get(0), i));
//                    }
//                }
//                for (int i = 0; i < getCartas().size(); i++) {
//                    if (getCartas().get(i).size() != 0) {
//                        if (topOfMazo(i).type.equals(Carta.Type.SHIELD)) {
//                            shieldsYo.add(new Pair<>(topOfMazo(i), i));
//                        } else {
//                            topsYo.add(new Pair<>(topOfMazo(i), i));
//                        }
//                    }
//                }
//
//                for (Pair<Carta, Integer> pE : topsEl) {
//                    for (Pair<Carta, Integer> pY : topsYo) {
//                        if (pE.first != null && pY.first.beatsOff(pE.first) && !pE.first.beatsDef(pY.first)) {
//                            callback.onTurnResolved(pY.second, pY.first, pE.second);
//                            return;
//                        }
//                    }
//                    for (Pair<Carta, Integer> pY : topsYo) {
//                        if (pE.first != null && pY.first.beatsOff(pE.first)) {
//                            callback.onTurnResolved(pY.second, pY.first, pE.second);
//                            return;
//                        }
//                    }
//                }
//                callback.onTurnResolved(topsYo.get(0).second, topsYo.get(0).first, topsEl.get(0).second);
//            }
//        }, 2000);
    }

    private int[] buscarJugadas(ArrayList<int[]> posibles) {
        for (int[] posible : posibles) {
            ArrayList<CeldaView.State> estado = mGame.getTableroState(posible[0]);
            estado.set(posible[1], CeldaView.State.PLAYER_2);
            if (Game.isTableroWon(estado)) {
                return new int[]{posible[0], posible[1]};
            }
        }

        for (int[] posible : posibles) {
            ArrayList<CeldaView.State> estado = mGame.getTableroState(posible[0]);
            estado.set(posible[1], CeldaView.State.PLAYER_1);
            if (Game.isTableroWon(estado)) {
                return new int[]{posible[0], posible[1]};
            }
        }

        return posibles.get(new Random().nextInt(posibles.size()));
    }

    public void setGame(Game game) {
        mGame = game;
    }
}
