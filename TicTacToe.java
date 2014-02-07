package distsys;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.rmi.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * A Tic Tac Toe application.
 * Currently this is a stand-alone application where
 * players take alternating turns using the same computer.
 * <p/>
 * The task is to transform it to a networking application using RMI.
 */
public class TicTacToe extends JFrame implements ListSelectionListener
{
  private static final int BOARD_SIZE = 15;
  private final BoardModel boardModel;
  private final JTable board;
  private final JLabel statusLabel = new JLabel();
  private final char playerMarks[] = {'X', 'O'};
  private int currentPlayer = 0; // Player to set the next mark.

  ConnectionInterface server;
  Connection client;

  public static void main(String args[])
  {
    try {
      new TicTacToe();
    }
    catch(Exception e) {
        e.printStackTrace();
    }
  }

  public TicTacToe() throws RemoteException
  {
    super("TDT4190: Tic Tac Toe");

    boardModel = new BoardModel(BOARD_SIZE);
    board = new JTable(boardModel);
    board.setFont(board.getFont().deriveFont(25.0f));
    board.setRowHeight(30);
    board.setCellSelectionEnabled(true);
    for (int i = 0; i < board.getColumnCount(); i++)
      board.getColumnModel().getColumn(i).setPreferredWidth(30);
    board.setGridColor(Color.BLACK);
    board.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    DefaultTableCellRenderer dtcl = new DefaultTableCellRenderer();
    dtcl.setHorizontalAlignment(SwingConstants.CENTER);
    board.setDefaultRenderer(Object.class, dtcl);
    board.getSelectionModel().addListSelectionListener(this);
    board.getColumnModel().getSelectionModel().addListSelectionListener(this);

    statusLabel.setPreferredSize(new Dimension(statusLabel.getPreferredSize().width, 40));
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(board, BorderLayout.CENTER);
    contentPane.add(statusLabel, BorderLayout.SOUTH);
    pack();

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    int centerX = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getSize().width) / 2;
    int centerY = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getSize().height) / 2;
    setLocation(centerX, centerY);
    setVisible(true);

    String address = "localhost:5001";
    String url = "rmi://" + address + "/ConnectionInterface";

    // Finner server
    try {
        server = (ConnectionInterface) Naming.lookup(url);

    } catch (NotBoundException nbe) {
        System.err.println("Ingen LaaneTjener er registrert!");
    } catch (ConnectException ce) {
        System.err.println("Fant ikke RMI registry på adressen "+ address);
    } catch (Exception e) {
        System.err.println("En uventet feil oppsto: " + e.getMessage());
    }

      try {
      client = new Connection(this);
      }catch(Exception e) {
          System.out.println("Kunne ikke opprette klient");
      }
      if(server == null) {
              System.out.println("Server started");

              try {
                  LocateRegistry.createRegistry(5001);

              }
              catch (RemoteException e) {
                  System.out.println("Java RMI registry already exists");
              }catch(Exception e) {
                  System.out.println("Detta funka hvertfall ikke..");
              }

              try {
                  Naming.rebind(url, client);
                  server = (ConnectionInterface) Naming.lookup(url);
                  System.out.println("Shit. It's working!");
              }
              catch (Exception e) {
                  System.err.println("MIH");
              }

      }else {
          System.out.println("Server found");

          try {
              server.passServer(client);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  }

  void setStatusMessage(String status)
  {
    statusLabel.setText(status);
  }

  public void setServer(Connection server) {
      this.server = server;
  }

  /**
   * This has to be modified. Currently the application is stand-alone so
   * both players have to use the same computer.
   * <p/>
   * When completed, marks from the first player originates from a ListSelectionEvent
   * and is then sent to the second player. And marks from the second player is received
   * and added to the board of the first player.
   */
  public void valueChanged(ListSelectionEvent e)
  {
    if (e.getValueIsAdjusting())
      return;
    int x = board.getSelectedColumn();
    int y = board.getSelectedRow();
    if (x == -1 || y == -1 || !boardModel.isEmpty(x, y))
      return;
    if (boardModel.setCell(x, y, playerMarks[currentPlayer]))
      setStatusMessage("Player " + playerMarks[currentPlayer] + " won!");
    currentPlayer = 1 - currentPlayer; // The next turn is by the other player.

    try {
        server.remoteChange(x, y, playerMarks[currentPlayer]);
    }catch(RemoteException re) {
        re.printStackTrace();
     }
  }

    public void remoteChange(int x, int y, char mark) {
        if (boardModel.setCell(x, y, mark))
            setStatusMessage("Player " + playerMarks[currentPlayer] + " won!");
        currentPlayer = 1 - currentPlayer; // The next turn is by the other player.
    }
}
