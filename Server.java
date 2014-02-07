/*package distsys;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server
{

    public Server() {}

    public String doMove() {
        return "ConnectionInterface, world!";
    }

    public static void main(String args[]) {

        try {
            TicTacToe obj = new TicTacToe();
            ConnectionInterface stub = (ConnectionInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("ConnectionInterface", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}   */
