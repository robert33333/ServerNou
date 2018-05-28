package date;

import java.io.Serializable;

/**
 * Created by Rob on 06.05.2018.
 */
public class Pariu implements Serializable {
    private final Meci meci;
    private final String optiune;

    public Pariu(Meci meci, String optiune) {
        this.meci = meci;
        this.optiune = optiune;
    }

    public Meci getMeci() {
        return meci;
    }

    public String getOptiune() {
        return optiune;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pariu pariu = (Pariu) o;

        return (meci != null ? meci.equals(pariu.meci) : pariu.meci == null) && (optiune != null ? optiune.equals(pariu.optiune) : pariu.optiune == null);
    }

    @Override
    public int hashCode() {
        int result = meci != null ? meci.hashCode() : 0;
        result = 31 * result + (optiune != null ? optiune.hashCode() : 0);
        return result;
    }
}
