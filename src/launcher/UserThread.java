package launcher;

import date.Bilet;
import date.Comanda;
import date.Meci;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

class UserThread extends Thread {
    private final Socket socket;

    public UserThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        ObjectOutputStream oos;
        ObjectInputStream ois;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(Launcher.selectMeciuri());
        } catch (IOException | SQLException e) {
            return;
        }
        while (true) {
            try {
                Comanda comanda = (Comanda) ois.readObject();
                switch (comanda.getOptiune()) {
                    case "insert meci":
                        Launcher.insertMeci((Meci)comanda.getObj());
                        oos.writeObject(Launcher.selectMeciuri());
                        break;
                    case "adauga rezultat":
                        Launcher.updateMeci((Meci)comanda.getObj());
                        oos.writeObject(Launcher.selectMeciuri());
                        break;
                    case "refresh":
                        oos.writeObject(Launcher.selectMeciuri());
                        break;
                    case "insert bilet":
                        Launcher.insertBilet((Bilet)comanda.getObj());
                        oos.writeObject(Launcher.selectBilete());
                        break;
                    case "refresh bilet":
                        oos.writeObject(Launcher.selectBilete());
                        break;
                    case "validare bilet":
                        Launcher.validareBilet((int)comanda.getObj());
                        break;
                }
            } catch (IOException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

        }
    }
}