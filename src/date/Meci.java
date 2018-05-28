package date;

import java.io.Serializable;

/**
 * Created by Rob on 06.05.2018.
 */
public class Meci implements Serializable {
    private final int id;
    private final String echipa1;
    private final String echipa2;
    private String rezultat = "null";
    private final Double cota1;
    private final Double cota2;
    private final Double cotax;

    public Meci(int id, String echipa1, String echipa2, Double cota1, Double cotax, Double cota2) {
        this.id = id;
        this.echipa1 = echipa1;
        this.echipa2 = echipa2;
        this.cota1 = cota1;
        this.cota2 = cota2;
        this.cotax = cotax;
    }

    public int getId() {
        return id;
    }

    public String getEchipa1() {
        return echipa1;
    }

    public String getEchipa2() {
        return echipa2;
    }

    public String getRezultat() {
        return rezultat;
    }

    public void setRezultat(String rezultat) {
        this.rezultat = rezultat;
    }

    public Double getCota1() {
        return cota1;
    }

    public Double getCota2() {
        return cota2;
    }

    public Double getCotax() {
        return cotax;
    }

}
