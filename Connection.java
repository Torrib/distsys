package distsys;

import javax.swing.event.ListSelectionEvent;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
* Created by thb on 04.02.14.
*/
public class Connection extends UnicastRemoteObject implements ConnectionInterface
{
    private static final int BOARDSIZE = 10;

    private TicTacToe game;
    private char mark;

    public Connection(TicTacToe game) throws RemoteException
    {
        this.game = game;
    }

    @Override
    public void connect(String url) throws RemoteException
    {
        System.out.println("Server started");

        try {
            LocateRegistry.createRegistry(1314);
        }
        catch (RemoteException e) {
            System.out.println("Java RMI registry already exists");
        }
        try
        {
            Naming.rebind(url, this);
            System.out.println("Connected");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconect() throws RemoteException
    {
        game.disconect();
    }

    public void doMove(ListSelectionEvent e)
    {
        game.valueChanged(e);
    }

    public void passServer(Connection server)
    {
        game.setServer(server);
    }
}
