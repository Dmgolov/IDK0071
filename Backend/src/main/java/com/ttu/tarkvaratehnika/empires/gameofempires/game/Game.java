package com.ttu.tarkvaratehnika.empires.gameofempires.game;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Game {

    @Id
    private long gameId;

    @Column(nullable = false)
    private String winner;
    @Column(nullable = false)
    private String winnerColor;
    @Column(nullable = false)
    private Timestamp startTime;
    @Column(nullable = false)
    private Timestamp endTime;
    @Column(nullable = false)
    private int numOfTurns;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinnerColor() {
        return winnerColor;
    }

    public void setWinnerColor(String winnerColor) {
        this.winnerColor = winnerColor;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public int getNumOfTurns() {
        return numOfTurns;
    }

    public void setNumOfTurns(int numOfTurns) {
        this.numOfTurns = numOfTurns;
    }
}
