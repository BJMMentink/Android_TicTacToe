package edu.bjm.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Board {

    public final static int BOARDSIZE = 3;
    String[][] cellValues = new String[BOARDSIZE][BOARDSIZE];
    Rect[][] cells = new Rect[BOARDSIZE][BOARDSIZE];
    int SIZE = 55;
    int OFFSET = 10;
    public static final String TAG = "Board";
    int viewWidth;
    int viewHeight;
    private ComputerAI ai;
    boolean isAI = true;
    private Context context;

    public Board()
    {
        initCellValues();
        ai = new ComputerAI();
    }

    public Board(int width)
    {
        viewWidth = width / BOARDSIZE;
        viewHeight = viewWidth;
        SIZE = viewWidth - 3;
        ai = new ComputerAI();
        this.context = context;
        initCellValues();
    }

    public Board(int width, int height)
    {
        viewWidth = width / BOARDSIZE;
        viewHeight = height / BOARDSIZE;
        SIZE = viewWidth -3;
        initCellValues();
    }

    public void clearCellValues() {
        for (int row = 0; row < cells[0].length; row++)
        {
            for (int col = 0; col < cells[1].length; col++)
            {
                cellValues[row][col] = "";
            }
        }
    }

    public void initCellValues() {
        for (int row = 0; row < cells[0].length; row++)
        {
            for (int col = 0; col < cells[1].length; col++)
            {
                    cellValues[row][col] = "";
            }
        }
    }



    public String hitTest(Point point, String turn, Context context) {
        String result = "-1";

        for(int row = 0; row < BOARDSIZE; row++) {
            for(int col = 0; col < BOARDSIZE; col++) {
                if(cellValues[row][col].isEmpty()) {
                    if(cells[row][col].contains(point.x, point.y)) {
                        cellValues[row][col] = turn;
                        result = "0";
                    }
                }
            }
        }
        return result;
    }



    public void Draw(Canvas canvas)
    {
        // Draw the board.
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        for(int row = 0; row < cells[0].length; row++)
        {
            for(int col = 0; col < cells[1].length; col++)
            {
                paint.setColor(Color.WHITE);

                cells[row][col] = new Rect();
                cells[row][col].left = col * SIZE + OFFSET;
                cells[row][col].top = row * SIZE + OFFSET;
                cells[row][col].right = col * SIZE + OFFSET + SIZE;
                cells[row][col].bottom = row * SIZE + OFFSET + SIZE;

                canvas.drawRect(cells[row][col], paint);

                if(cellValues[row][col] == "1")
                    drawTurn(canvas, cells[row][col], 1);
                else if(cellValues[row][col] == "2")
                    drawTurn(canvas, cells[row][col], 2);

                Log.d(TAG, "Draw: Failed " + cellValues[row][col]);

            }
        }

        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setStrokeWidth(10);
        canvas.drawLine(viewWidth - viewWidth, viewHeight, viewWidth * 3 ,viewHeight, paint1);
        canvas.drawLine(viewWidth - viewWidth, viewHeight * 2, viewWidth * 3 ,viewHeight * 2, paint1);
        canvas.drawLine(viewWidth, viewHeight - viewHeight, viewWidth,viewHeight * 3, paint1);
        canvas.drawLine(viewWidth * 2, viewHeight - viewHeight, viewWidth * 2 ,viewHeight * 3, paint1);

    }

    private void drawTurn(Canvas canvas, Rect rect, int turn)
    {
        // Place a graphic on the board.
        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(7);
        paint.setStyle(Paint.Style.STROKE);

        int x = rect.centerX();
        int y = rect.centerY();
        if (isAI){
            if (turn == 1){
                canvas.drawLine(x - (rect.width() / 3), y - (rect.height() / 3), x + (rect.width() / 3),y + (rect.height() / 3), paint);
                canvas.drawLine(x + (rect.width() / 3), y - (rect.height() / 3), x - (rect.width() / 3),y + (rect.height() / 3), paint);
            }
            else {
                canvas.drawCircle(x, y, SIZE * .35f, paint);
            }
        }
        else {
            if (turn == 1){
                canvas.drawLine(x - (rect.width() / 3), y - (rect.height() / 3), x + (rect.width() / 3),y + (rect.height() / 3), paint);
                canvas.drawLine(x + (rect.width() / 3), y - (rect.height() / 3), x - (rect.width() / 3),y + (rect.height() / 3), paint);
            }
        }


    }
    public Point makeAIMove(String turn) {
        Point aiMove = ai.makeMove(cellValues, turn);
        if (aiMove != null) {
            cellValues[aiMove.x][aiMove.y] = turn;
            return aiMove;
        }
        return null;
    }
    public String checkForWinner() {
        for (int row = 0; row < BOARDSIZE; row++) {
            if (cellValues[row][0].equals(cellValues[row][1]) &&
                    cellValues[row][1].equals(cellValues[row][2]) &&
                    !cellValues[row][0].isEmpty()) {
                return cellValues[row][0];
            }
        }
        for (int col = 0; col < BOARDSIZE; col++) {
            if (cellValues[0][col].equals(cellValues[1][col]) &&
                    cellValues[1][col].equals(cellValues[2][col]) &&
                    !cellValues[0][col].isEmpty()) {
                return cellValues[0][col];
            }
        }
        if (cellValues[0][0].equals(cellValues[1][1]) &&
                cellValues[1][1].equals(cellValues[2][2]) &&
                !cellValues[0][0].isEmpty()) {
            return cellValues[0][0];
        }
        if (cellValues[0][2].equals(cellValues[1][1]) &&
                cellValues[1][1].equals(cellValues[2][0]) &&
                !cellValues[0][2].isEmpty()) {
            return cellValues[0][2];
        }

        return null;
    }
    public int drawGameState(Canvas canvas, String gameState) {
        String[] state = gameState.split("\\|");
        int index = 0;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(7);
        paint.setStyle(Paint.Style.STROKE);

        int turns = 0;

        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = 0; col < BOARDSIZE; col++) {
                index++;

                Rect rect = cells[row][col];
                int x = rect.centerX();
                int y = rect.centerY();

                String cellValue = state[index - 1];

                if (!cellValue.isEmpty()) {
                    if (cellValue.equals("X")) {
                        drawTurn(canvas, cells[row][col], 1);
                        turns++;
                    } else if (cellValue.equals("O")) {
                        drawTurn(canvas, cells[row][col], 2);
                        turns++;
                    }

                }
            }
        }

        return turns;
    }



}

