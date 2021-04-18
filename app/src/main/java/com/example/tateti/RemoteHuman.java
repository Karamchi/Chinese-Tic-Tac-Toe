package com.example.tateti;

import com.example.roommanager.RoomManager;
import com.example.tateti.model.Game;
import com.example.tateti.model.Human;

public class RemoteHuman extends Human {

    @Override
    public void doTurn(final Game.TurnRequestedCallback callback) {
        RoomManager.getInstance().registerCallback(new RoomManager.Callback() {
            @Override
            public void onMessageReceived(String s, long timestamp) {
                String[] split = s.split(" ");
                if (split.length != 2) return;

                final int tablero = Integer.parseInt(split[0]);
                final int celda = Integer.parseInt(split[1]);

                callback.onTurnResolved(tablero, celda);
            }
        });
    }
}
