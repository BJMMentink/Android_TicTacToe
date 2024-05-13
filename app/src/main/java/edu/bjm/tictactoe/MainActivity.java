package edu.bjm.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.ArrayList;
import java.util.Objects;

/*
https://snippets.cacher.io/snippet/875030698b29c73f56db
 */



public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    HubConnection hubConnection;
    String hubConnectionId;
    Game game = new Game();
    String opponentName;
    int width;
    int height;
    int turnCount = 0;
    Board board;
    Canvas MainCanvas;
    Point point  = new Point();
    Boolean isWon = false;
    boolean isAI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if (intent != null) {
            if(intent.hasExtra("gameId")){
                String gameId = intent.getStringExtra("gameId");
                loadGame(gameId);
                Log.d(TAG, "onCreate: " + game.toString());

            }else{
                String hubConnectionId = intent.getStringExtra("gameName");
                opponentName = intent.getStringExtra("opponentName");
                isAI = "Computer".equals(opponentName);
                TextView textPlayerName = findViewById(R.id.txtPlayerName);
                textPlayerName.setText(hubConnectionId);
            }
        }
        initSignalR();
        getScreenDims();
        board = new Board(width);



        Button buttonSaveExit = findViewById(R.id.btnSaveExit);
        buttonSaveExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        FrameLayout drawViewContainer = findViewById(R.id.drawView_container);
        DrawView drawView = new DrawView(this);
        drawViewContainer.addView(drawView);


        Log.d(TAG, "onCreate: End");
    }


    public void loadGame(String gameId) {
        String url = getString(R.string.gameTracker) + gameId;
        RestClient.execGetOneRequest(url, this, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<Game> games) {
                if (!games.isEmpty()) {
                    Game tempgame = games.get(0);
                    game = new Game(
                            tempgame.getId(),
                            tempgame.getConnectionId(),
                            tempgame.getPlayer1(),
                            tempgame.getPlayer2(),
                            tempgame.getWinner(),
                            tempgame.getNextTurn(),
                            tempgame.isCompleted(),
                            tempgame.getLastUpdateDate(),
                            tempgame.getGameState()
                    );
                    hubConnectionId = game.getConnectionId();
                    opponentName = game.getPlayer2();
                    isAI = "Computer".equals(opponentName);
                    TextView textPlayerName = findViewById(R.id.txtPlayerName);
                    textPlayerName.setText(hubConnectionId);
                    Log.d(TAG, "onSuccess: " + game.getConnectionId());

                } else {
                    Toast.makeText(MainActivity.this, "Game not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void getScreenDims() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        Log.d(TAG, "getScreenDims: " + width + ":" + height);
    }

    private void initSignalR() {
        hubConnection = HubConnectionBuilder.create("https://fvtcdp.azurewebsites.net/GameHub").build();
        Log.d(TAG, "initSignalR: Hub Built");

        hubConnection.start().blockingAwait();
        hubConnection.invoke(Void.class, "GetConnectionId");

        hubConnection.on("ReceiveMessage", (user, message) -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: User: " + user);
                    Log.d(TAG, "run: Message: " + message);

                    String[] data = user.split(":", 3);
                    setContentView(new DrawView(getMainActivity(),
                            Integer.valueOf(data[1]),
                            Integer.valueOf(data[2])));

                }
            });
        }, String.class, String.class);
    }

    private MainActivity getMainActivity()
    {
        return this;
    }
    private class DrawView extends View implements View.OnTouchListener{
        int posX = 140;
        int posY = 130;

        Boolean isDragging;

        String turn = "1";


        public DrawView(Context context) {
            super(context);
            this.setOnTouchListener(this);
        }

        public DrawView(Context context, int x, int y) {
            super(context);
            this.setOnTouchListener(this);
            posX = x;
            posY = y;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isWon) {
                        Point touchPoint = new Point(x, y);
                        String hitResult = board.hitTest(touchPoint, turn, getMainActivity());
                        if (hitResult.equals("0")) {
                            turn = (turn.equals("1")) ? "2" : "1";
                            turnCount++;
                            String winner = board.checkForWinner();
                            invalidate();
                            if (winner != null || turnCount == 9) {
                                handleWinner(winner);
                            }
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (isAI && !isWon) {
                        String winner = board.checkForWinner();
                        if (winner == null) {
                            Point aiMove = board.makeAIMove(turn);
                            if (aiMove != null) {
                                turn = (turn.equals("1")) ? "2" : "1";
                                turnCount++;
                                winner = board.checkForWinner();
                                invalidate();
                                if (winner != null) {
                                    handleWinner(winner);
                                }
                            }
                        }
                    }
                    break;
            }
            return true;
        }


        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.drawColor(Color.DKGRAY);
            MainCanvas = canvas;
            board.Draw(canvas);
            if (game.getGameState() != null && turnCount == 0){
                turnCount = board.drawGameState(canvas, game.getGameState());
                Log.d(TAG, "Number of affected rows: " + turnCount);
            }

        }
        private void handleWinner(String winner) {
            if (turnCount == 9){
                Log.d(TAG, "No winner -- Draw");
                isWon = true;
            }else{
                Log.d(TAG, "Winner is Player " + winner);
                isWon = true;
            }
            game.setCompleted(true);
            game.setWinner(winner);
            RestClient.execPutRequest(game,getString(R.string.gameTracker) + game.getId(), MainActivity.this,  new VolleyCallback() {
                @Override
                public void onSuccess(ArrayList<Game> games) {
                    Log.d(TAG, "New game created successfully");
                    Intent intent = new Intent(MainActivity.this, GamesListActivity.class);
                    startActivity(intent);
                }
            });

        }
    }
}