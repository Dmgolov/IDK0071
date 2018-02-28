package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import java.util.Objects;

public class Coordinates {

    private int x, y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        int hashCode = 31;
        hashCode += hashCode * x;
        hashCode += hashCode * y;
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Coordinates
                && this.x == ((Coordinates) obj).getX()
                && this.y == ((Coordinates) obj).getY()
                && this.hashCode() == obj.hashCode();
    }
}