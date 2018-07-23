package app.tfkproject.taekwondoclient.Model;

/**
 * Created by taufik on 31/05/18.
 */

public class Score {
    public int biru, merah;

    public Score(){

    }

    public Score(int biru, int merah){
        this.biru = biru;
        this.merah = merah;
    }

    public int getBiru() {
        return biru;
    }

    public int getMerah() {
        return merah;
    }
}