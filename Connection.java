package distsys;

import javax.swing.event.ListSelectionEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by thb on 04.02.14.
 */
public class Connection extends UnicastRemoteObject implements ConnectionInterface
{
    TicTacToe game;

    public Connection(TicTacToe game) throws RemoteException {
        this.game = game;
    }

    @Override
    public boolean connect(String ip, int port) throws java.rmi.RemoteException {
        return false;
    }

    @Override
    public boolean disconnect() throws java.rmi.RemoteException {
        return false;
    }

    @Override
    public void remoteChange(int x, int y, char mark) {
        game.remoteChange(x, y, mark);
    }

    public void passServer(Connection server)
    {
        game.setServer(server);
    }
}
