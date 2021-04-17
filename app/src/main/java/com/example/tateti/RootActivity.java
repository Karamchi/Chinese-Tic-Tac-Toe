package com.example.tateti;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tateti.dialog.CreateDialog;

import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RootActivity extends AppCompatActivity {

    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        findViewById(R.id.play_vs_ai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setActivated(true);
                enableButtons(false);
                Intent intent = new Intent(RootActivity.this, MainActivity.class);
                intent.putExtra("AI", true);
                startActivity(intent);
            }
        });
        findViewById(R.id.root_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateDialog();
            }
        });
        findViewById(R.id.root_match).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setActivated(true);
                enableButtons(false);
                showLoading();
                RoomManager.getAllRooms(new Callback<Map<String, List<String>>>() {
                    @Override
                    public void onResponse(Call<Map<String, List<String>>> call, Response<Map<String, List<String>>> response) {
                        Log.e("Request", call.request().url().toString());
                        hideLoading();
                        startInRoom(getRoom(response.body()));
                    }

                    @Override
                    public void onFailure(Call<Map<String, List<String>>> call, Throwable t) {
                        Toast.makeText(RootActivity.this, "Can't connect to the server", Toast.LENGTH_SHORT).show();
                        hideLoading();
                    }
                });
            }
        });
        findViewById(R.id.root_howto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RootActivity.this, HowToActivity.class);
                startActivity(intent);
            }
        });
    }

    private String getRoom(Map<String, List<String>> body) {
        for (String room : body.keySet()) {
            if (body.get(room).size() < 2) {
                return room;
            }
        }
        return random();
    }

    private static String random() {
        String s = "";
        for (int i = 0; i < 5; i++) {
            s += Integer.toString((Math.abs(new Random().nextInt() % 10)));
        }
        return "observable-" + s;
    }

    private void showCreateDialog() {
        CreateDialog dialog = new CreateDialog(RootActivity.this);
        dialog.setOnFinishListener(new CreateDialog.OnFinishListener() {
            @Override
            public void onFinish(String s) {
                findViewById(R.id.root_create).setActivated(true);
                enableButtons(false);
                startInRoom("observable-private-" + s);
            }
        });
        dialog.show();
    }

    private void showLoading() {
        isLoading = true;
        findViewById(R.id.logo).setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        isLoading = false;
        findViewById(R.id.logo).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        isLoading = false;
        enableButtons(true);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int view : new int[]{R.id.play_vs_ai, R.id.root_match, R.id.root_create}) {
            findViewById(view).setActivated(false);
        }
        enableButtons(true);
    }

    private void startInRoom(String room) {
        RoomManager.getInstance().connectToRoom(room);
        Intent intent = new Intent(RootActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void enableButtons(boolean enable) {
        for (int view : new int[]{R.id.play_vs_ai, R.id.root_match, R.id.root_create}) {
            findViewById(view).setEnabled(enable);
        }
    }

}
