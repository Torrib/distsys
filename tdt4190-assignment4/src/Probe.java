/**
 * Created with IntelliJ IDEA.
 * User: lizter
 * Date: 16/03/14
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The messages are send asynchronously to avoid concurrency issues.
 * The method invocation is for that reason done in its own thread.
 */


public class Probe extends Thread {
    /**
     *
     */
    private final ServerImpl server;
    /**
     * Queue containing does resourceIDs we are waiting for
     */
    private final Queue <Integer> waitingFor = new LinkedList<Integer>();

    /**
     * Allocates a probe object with the an specfied remote object as input parameter
     * @param server
     */
    public Probe(ServerImpl server){
        this.server = server;
    }

    /**
     * Calls receiveProbe() on the remote server reference
     */
    public void run(){
        try {
            server.receiveProbe(waitingFor);
        }catch(RemoteException e){
            System.err.println("RemoteException ERROR");
        }
    }

}