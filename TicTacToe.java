package distsys;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.rmi.*;

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
    private boolean myTurn = false;
    private ConnectionInterface server;
    private Connection client;
//  private Connection connection;

  public static void main(String args[])
  {
      String address = "localhost";


      System.setSecurityManager( new CustomSecurity() );

      TicTacToe game = new TicTacToe();
      game.initConnections(address);
  }

  public TicTacToe()
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
  }

  void setStatusMessage(String status)
  {
    statusLabel.setText(status);
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
      if(!myTurn)
          return;
    if (e.getValueIsAdjusting())
      return;
    int x = board.getSelectedColumn();
    int y = board.getSelectedRow();
    if (x == -1 || y == -1 || !boardModel.isEmpty(x, y))
      return;
    setMark(x, y, currentPlayer);
      try {
          server.doMove(x, y, currentPlayer);
      } catch (RemoteException e1) {
          e1.printStackTrace();
      }
      myTurn = false;
  }

    public void setMark(int x, int y, int player)
    {
        if (boardModel.setCell(x, y, playerMarks[player]))
            setStatusMessage("Player " + playerMarks[player] + " won!");

    }

    public void setMyTurn(boolean myTurn)
    {
        this.myTurn = myTurn;
    }


    public void disconect()
    {
        server = null;
    }

    public void setServer(ConnectionInterface server)
    {
        this.server = server;
    }

    private void initConnections(String address)
    {
        String url = "rmi://" + address + "/derp";

        try {
            server = (ConnectionInterface) Naming.lookup(url);
        }
        catch (NotBoundException nbe) {
            System.err.println("No server in RMI registry");
        }
        catch (ConnectException ce) {
            System.err.println("No RMI registry found at " + address);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            client = new Connection(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (server == null)
        {
            System.err.println("Server not found");
            try {
                client.connect(url);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Server found");

            try {
                server.passServer(client);
                server.passPlayerID(1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayerID(int id)
    {
        currentPlayer = id;
    }
}
