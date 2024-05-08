package edu.bjm.tictactoe;

import android.graphics.Point;

public class ComputerAI {

    public Point makeMove(String[][] cellValues, String turn) {
        Point winningMove = findWinningMove(cellValues, turn);
        if (winningMove != null) {
            return winningMove;
        }
        Point blockingMove = findBlockingMove(cellValues, turn);
        if (blockingMove != null) {
            return blockingMove;
        }
        Point optimalMove = findMove(cellValues);
        if (optimalMove != null) {
            return optimalMove;
        }
        return findEmptyCell(cellValues);
    }
    private Point findBlockingMove(String[][] cellValues, String turn) {
        String opponent = (turn.equals("1")) ? "2" : "1";
        return findWinningMove(cellValues, opponent);
    }
    private Point findWinningMove(String[][] cellValues, String turn) {
        String opponent = (turn.equals("1")) ? "2" : "1";
        for (int i = 0; i < 3; i++) {
            if (cellValues[i][0].equals(turn) && cellValues[i][1].equals(turn) && cellValues[i][2].isEmpty()) {
                return new Point(i, 2);
            }
            if (cellValues[i][0].equals(turn) && cellValues[i][2].equals(turn) && cellValues[i][1].isEmpty()) {
                return new Point(i, 1);
            }
            if (cellValues[i][1].equals(turn) && cellValues[i][2].equals(turn) && cellValues[i][0].isEmpty()) {
                return new Point(i, 0);
            }
            if (cellValues[0][i].equals(turn) && cellValues[1][i].equals(turn) && cellValues[2][i].isEmpty()) {
                return new Point(2, i);
            }
            if (cellValues[0][i].equals(turn) && cellValues[2][i].equals(turn) && cellValues[1][i].isEmpty()) {
                return new Point(1, i);
            }
            if (cellValues[1][i].equals(turn) && cellValues[2][i].equals(turn) && cellValues[0][i].isEmpty()) {
                return new Point(0, i);
            }
        }
        if (cellValues[0][0].equals(turn) && cellValues[1][1].equals(turn) && cellValues[2][2].isEmpty()) {
            return new Point(2, 2);
        }
        if (cellValues[0][0].equals(turn) && cellValues[2][2].equals(turn) && cellValues[1][1].isEmpty()) {
            return new Point(1, 1);
        }
        if (cellValues[1][1].equals(turn) && cellValues[2][2].equals(turn) && cellValues[0][0].isEmpty()) {
            return new Point(0, 0);
        }
        if (cellValues[0][2].equals(turn) && cellValues[1][1].equals(turn) && cellValues[2][0].isEmpty()) {
            return new Point(2, 0);
        }
        if (cellValues[0][2].equals(turn) && cellValues[2][0].equals(turn) && cellValues[1][1].isEmpty()) {
            return new Point(1, 1);
        }
        if (cellValues[1][1].equals(turn) && cellValues[2][0].equals(turn) && cellValues[0][2].isEmpty()) {
            return new Point(0, 2);
        }
        return null;
    }
    private Point findMove(String[][] cellValues) {
        if (cellValues[1][1].isEmpty()) {
            return new Point(1, 1);
        }
        if (cellValues[0][0].isEmpty()) {
            return new Point(0, 0);
        }
        if (cellValues[0][2].isEmpty()) {
            return new Point(0, 2);
        }
        if (cellValues[2][0].isEmpty()) {
            return new Point(2, 0);
        }
        if (cellValues[2][2].isEmpty()) {
            return new Point(2, 2);
        }
        if (cellValues[0][1].isEmpty()) {
            return new Point(0, 1);
        }
        if (cellValues[1][0].isEmpty()) {
            return new Point(1, 0);
        }
        if (cellValues[1][2].isEmpty()) {
            return new Point(1, 2);
        }
        if (cellValues[2][1].isEmpty()) {
            return new Point(2, 1);
        }

        return null;
    }
    private Point findEmptyCell(String[][] cellValues) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cellValues[i][j].isEmpty()) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }
}
