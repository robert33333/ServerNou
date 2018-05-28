package date;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rob on 06.05.2018.
 */
public class Bilet implements Serializable {
    private boolean dejaValidat = false;
    private final int id;
    private final ArrayList<Pariu> pariuri;
    private double bani;

    public ArrayList<Pariu> getPariuri() {
        return pariuri;
    }

    public Bilet(int idNum, ArrayList<Pariu> pariuri, double bani) {
        this.id = idNum;
        this.pariuri = pariuri;
        this.bani = bani;
    }

    public int getId() {
        return id;
    }

    public double getBani() {
        return bani;
    }

    public void setBani(double bani) {
        this.bani = bani;
    }

    public double calculeazaCastigPotential() {
        double castig = bani;
        for (Pariu pariu : pariuri) {
            switch (pariu.getOptiune()) {
                case "1" :
                    castig *= pariu.getMeci().getCota1();
                    break;
                case "x" :
                    castig *= pariu.getMeci().getCotax();
                    break;
                case "2" :
                    castig *= pariu.getMeci().getCota2();
                    break;
            }
        }
        return castig;
    }

    public boolean verificaBilet() {
        for (Pariu pariu : pariuri) {
            if (!pariu.getOptiune().equals(pariu.getMeci().getRezultat()))
                return false;
        }
        return true;
    }

    public boolean isDejaValidat() {
        return dejaValidat;
    }

    public void setDejaValidat() {
        this.dejaValidat = true;
    }
}
