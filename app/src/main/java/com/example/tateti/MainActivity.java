package com.example.tateti;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roommanager.RoomManager;
import com.example.tateti.model.AI;
import com.example.tateti.model.Game;
import com.example.tateti.model.Human;
import com.example.tateti.view.CeldaView;
import com.example.tateti.view.TableroView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private @IdRes int[] tablero_ints = {R.id.tablero_11, R.id.tablero_12, R.id.tablero_13, R.id.tablero_21, R.id.tablero_22, R.id.tablero_23, R.id.tablero_31, R.id.tablero_32, R.id.tablero_33};

    private ArrayList<TableroView> tableros = new ArrayList<>();

    private Game game;
    private TextView mTextStatus;
    private MediaPlayer mp;
    private boolean amX;

    private @IdRes int celdap1 = R.id.celda_1p;
    private @IdRes int celdap2 = R.id.celda_2p;

    //TODO:
    // P2P specific
    // 2 - connectivity listener (or at least handle failures)
    // howto
    // for both
    // bugs

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextStatus = findViewById(R.id.title);
        mTextStatus.setText("Waiting for opponent to join...");

        for (int i = 0; i < tablero_ints.length; i++) {
            tableros.add((TableroView) findViewById(tablero_ints[i]));
        }

        if (getIntent().getBooleanExtra("AI", false)) {
            AI ai = new AI();
            game = new Game(new Human(), ai);
            ai.setGame(game);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    readyPlayer1();
                    updateTableros();
                    setLasdeabajo();
                }
            }, 1);
        } else {
            game = new Game(new Human(), new RemoteHuman());
        }

        setListeners();
        RoomManager.getInstance().setMembershipListener(new RoomManager.MembershipListener() {
            @Override
            public void onMembers(int size) {
                Log.e(Integer.toString(size),"members");
                if (game.getState().equals(State.NOT_STARTED) && size == 1)
                    amX = true;
                if (game.getState().equals(State.NOT_STARTED) && size == 2)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (amX) readyPlayer1();
                            else readyPlayer2();
                            updateTableros();
                            setLasdeabajo();
                        }
                    });

            }

            @Override
            public void onMemberLeave() {
                if (game.getState().equals(State.NOT_STARTED)) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextStatus.setText("Opponent disconnected");
                        RoomManager.getInstance().disconnect();
                    }
                });
                game.waiting();
            }
        });

        mp = MediaPlayer.create(this, R.raw.exploracion_4);
        mp.setLooping(true);
//        mp.start();
    }

    private void setLasdeabajo() {
        ((CeldaView) findViewById(celdap1)).setSymbol(amX);
        ((CeldaView) findViewById(celdap2)).setSymbol(!amX);
    }

    public void readyPlayer1() {
        amX = true;
        game.player1();
        mTextStatus.setText("");
    }

    public void readyPlayer2() {
        game.player2(getAwayPlayerCallback());
        mTextStatus.setText("Waiting for opponent to play...");
    }

    public void setListeners() {
        for (int tablero = 0; tablero < tableros.size(); tablero++) {
            final int tablerof = tablero;
            tableros.get(tablero).setListeners(new TableroView.OnCeldaClickeadaCallback() {
                @Override
                public void onCeldaClickeada(int celda) {
                    celdaClickeada(tablerof, celda);
                }
            });
        }
    }

    public void celdaClickeada(final int tablero, int celda) {
        if (game.getState() != State.MI_TURNO) return;
        if (!game.isvalidPlay(tablero, celda)) return;
        finishResolveAttack(tablero, celda);
//        playSound(game.top.getSound());

    }

    public void finishResolveAttack(int tablero, int celda) {
        RoomManager.getInstance().sendMessage(tablero + " " + celda);
        tableros.get(tablero).play(celda, amX);
        game.onAttackResolved(tablero, celda, getAwayPlayerCallback());
        mTextStatus.setText("Waiting for opponent to play...");
        if (game.getState() == State.NOT_STARTED) mTextStatus.setText("Game Over");
        updateTableros();
    }

    private Game.TurnRequestedCallback getAwayPlayerCallback() {
        return new Game.TurnRequestedCallback() {
            @Override
            public void onTurnResolved(final int tablero, final int celda) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tableros.get(tablero).play(celda, !amX);
                        game.onDefense(tablero, celda);
                        updateTableros();
                        //RoomManager.getInstance().sendMessage(game.tableroJugable.toString());
                        mTextStatus.setText("");
                        if (game.getState() == State.NOT_STARTED) mTextStatus.setText("Game Over");
//                        playSound(carta.getSound());
                    }
                });
            }
        };
    }

    public void updateTableros() {
        for (int i = 0; i < Game.CELDAS; i++) {
            tableros.get(i).setEnabled(false);
            tableros.get(i).setEnabled(game.getState(i) == CeldaView.State.EMPTY
                    && (game.tableroJugable == null || game.tableroJugable == i));
            if (game.getState(i) == CeldaView.State.PLAYER_1 || game.getState(i) == CeldaView.State.PLAYER_2) {
                tableros.get(i).setWon((game.getState(i) == CeldaView.State.PLAYER_1) == amX);
            }
            tableros.get(i).setClickable(game.getState() == State.MI_TURNO);
        }
        ((View)findViewById(celdap1).getParent()).setAlpha(game.getState() == State.WAITING_FOR_AWAY_PLAYER ? 0.5f : 1f);
        ((View)findViewById(celdap2).getParent()).setAlpha(game.getState() == State.MI_TURNO ? 0.5f : 1f);
    }

    @Override
    public void onBackPressed() {
        if (State.NOT_STARTED.equals(game.getState())) {
            super.onBackPressed();
            return;
        }
        new AlertDialog.Builder(this).setTitle("Abandon the game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        RoomManager.getInstance().disconnect();
        super.onDestroy();
        mp.stop();
    }

    private void playSound(@RawRes Integer sound) {
        if (sound == null) return;
        MediaPlayer mp = MediaPlayer.create(this, sound);
        mp.start();
    }

}
