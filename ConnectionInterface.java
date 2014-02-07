package distsys;
/**
 * Created by thb on 03.02.14.
 */
import javax.swing.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.swing.event.ListSelectionEvent;

public interface ConnectionInterface extends Remote {
    boolean connect(String ip, int port) throws RemoteException;
    boolean disconnect() throws RemoteException;
    void remoteChange(int x, int y, char mark) throws RemoteException;
    void passServer(Connection server) throws RemoteException;
}