package assignment1;

/**
 * Created by thb on 03.02.14.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConnectionInterface extends Remote {
    boolean connect(String ip, int port) throws RemoteException;

    boolean disconect() throws RemoteException;
}