package distsys;
/**
 * Created by thb on 03.02.14.
 */
import javax.swing.event.ListSelectionEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConnectionInterface extends Remote {

    void connect(String url) throws RemoteException;

    void disconect() throws RemoteException;

    void doMove(int x, int y, int currentPlayer) throws RemoteException;

    void passServer(ConnectionInterface server) throws RemoteException;

    void passPlayerID(int id) throws RemoteException;

}