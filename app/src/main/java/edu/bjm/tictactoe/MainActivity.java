package edu.bjm.tictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

/*
https://snippets.cacher.io/snippet/875030698b29c73f56db
 */



public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    HubConnection hubConnection;
    String hubConnectionId;
    int width;
    int height;
    Board board;
    Point point  = new Point();
    Boolean isWon = false;
    boolean isAI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new DrawView(this));


        //initSignalR();

        getScreenDims();
        board = new Board(width);

        Log.d(TAG, "onCreate: End");
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

            Log.d(TAG, "onTouch: " + x + ":" + y);

            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "onTouch: Down");

                    point.x = (int)event.getX();
                    point.y = (int)event.getY();
                    if (!isWon) {
                        if (board.hitTest(point, turn, getMainActivity()) != "-1") {
                            Log.d(TAG, "onTouch: " + point.x + " " + point.y);
                            turn = (turn.equals("1")) ? "2" : "1";
                            String winner = board.checkForWinner();
                            invalidate();
                            if (winner != null) {
                                handleWinner(winner);
                            }
                        }
                        invalidate();
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "onTouch: Move");

                    break;

                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "onTouch: Up");
                    //hubConnection.send("SendMessage", "Ben:" + (x-offsetX) + ":" + (y - offsetY), "Ben");
                    String winner = board.checkForWinner();
                    if(isAI && winner == null){
                        board.makeAIMove(turn);
                        turn = (turn.equals("1")) ? "2" : "1";
                        winner = board.checkForWinner();
                        invalidate();
                        if (winner != null) {
                            handleWinner(winner);
                        }
                    }
                    isDragging = false;
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.drawColor(Color.DKGRAY);
            board.Draw(canvas);

        }
        private void handleWinner(String winner) {
            Log.d(TAG, "Winner is Player " + winner);
            isWon = true;
        }
    }
}