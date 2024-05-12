package edu.bjm.tictactoe;

import static android.provider.Settings.System.getString;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GamesListActivity extends AppCompatActivity {
    public static final String TAG = "GamesListActivity";
    public static final String FILENAME = "teams.txt";
    ArrayList<Game> games;
    RecyclerView gameList;
    GamesAdapter gamesAdapter;
    public String player;
    public boolean autoLogout = false;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Game game = games.get(position);
            Log.d(TAG, "onClick: " + game.getPlayer1());
            Intent intent = new Intent(GamesListActivity.this, MainActivity.class);
            intent.putExtra("teamid", game.getId());
            Log.d(TAG, "onClick: " + game.getId());
            startActivity(intent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_list);
        Navbar.initListButton(this);
        Navbar.initGameButton(this);
        this.setTitle("List");
        games = new ArrayList<Game>();

        readFromAPI();

        initDeleteSwitch();
        initNewGameButton();
        if (autoLogout){
            Logout();
        }else{
            RebindGames();
        }


        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                double levelScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int batteryPercent = (int)Math.floor(batteryLevel / levelScale * 100);

                TextView txtBatteryLevel = findViewById(R.id.txtBatteryLevel);
                txtBatteryLevel.setText(batteryPercent + "%");
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver, filter);

        this.setTitle("Welcome " + player);
    }
    private void initNewGameButton() {
        Button btnNewGame = findViewById(R.id.btnNewGame);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: ");
                showAddGameDialog();
            }
        });
    }

    private void initDeleteSwitch() {
        SwitchCompat switchDelete = findViewById(R.id.switchDelete);
        switchDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                gamesAdapter.setDelete(isChecked);
                gamesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void RebindGames() {
        // Rebind the RecyclerView
        Log.d(TAG, "RebindTeams: Start");
        gameList = findViewById(R.id.rvGames);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        gameList.setLayoutManager(layoutManager);
        gamesAdapter = new GamesAdapter(games, this);
        gamesAdapter.setOnItemClickListener(onClickListener);
        gameList.setAdapter(gamesAdapter);

    }
    private void readFromAPI(){
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.gamePrefs), Context.MODE_PRIVATE);
            player = sharedPreferences.getString("player", null);
            if (player != null){
                Log.d(TAG, "readFromAPI: Start API");
                RestClient.execGetRequest(getString(R.string.gameTracker), this, new VolleyCallback() {
                    @Override
                    public void onSuccess(ArrayList<Game> results) {
                        Log.d(TAG, "onSuccess: Got here");
                        games = results;
                        RebindGames();
                    }
                });
            }
            else{
                Logout();
            }
        }catch (Exception e){
            Log.e(TAG, "readFromAPI: Error " + e.getMessage());
        }
    }


    private void showAddGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_game, null);
        final EditText editTextGameName = dialogView.findViewById(R.id.editTextGameName);
        final Switch switchPlayComputer = dialogView.findViewById(R.id.switchPlayComputer);

        builder.setView(dialogView)
                .setTitle("Add New Game")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gameName = editTextGameName.getText().toString().trim();
                        String p2Name = "";
                        boolean playAgainstComputer = switchPlayComputer.isChecked();
                        String opponentName = playAgainstComputer ? "Computer" : "";
                        Intent intent = new Intent(GamesListActivity.this, MainActivity.class);
                        intent.putExtra("id", -1);
                        intent.putExtra("gameName", gameName);
                        intent.putExtra("opponentName", opponentName);
                        Toast.makeText(GamesListActivity.this, "New game added: " + gameName + " against " + p2Name, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void Logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.gamePrefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("player", null);
        editor.apply();
        AddPlayerDialog();
    }
    private void AddPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Player");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPlayer = input.getText().toString().trim();
                if (!newPlayer.isEmpty()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.gamePrefs), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("player", newPlayer);
                    editor.apply();
                    readFromAPI();
                } else {
                    Toast.makeText(GamesListActivity.this, "Please input your Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
        RebindGames();
    }
}