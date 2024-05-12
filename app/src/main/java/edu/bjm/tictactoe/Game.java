package edu.bjm.tictactoe;

import java.util.Date;

public class Game {
    private String id;
    private String connectionId;
    private String player1;
    private String player2;
    private String winner;
    private String nextTurn;
    private Date lastUpdateDate;
    private boolean completed;
    private String gameState;

    public Game() {
        this.id = "";
        this.connectionId = "";
        this.player1 = "";
        this.player2 = "";
        this.winner = "";
        this.nextTurn = "";
        this.lastUpdateDate = new Date();
    }
    public Game(String id, String connectionId, String player1, String player2, String winner, String nextTurn, Date lastUpdateDate) {
        this.id = id;
        this.connectionId = connectionId;
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.nextTurn = nextTurn;
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(String nextTurn) {
        this.nextTurn = nextTurn;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public String getGameState() {
        return gameState;
    }
}
