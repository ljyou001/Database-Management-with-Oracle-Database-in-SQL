import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.*;
import java.util.Properties;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
/**
 * This is a flight manager to support: (1) add a flight (2) delete a flight (by
 * flight_no) (3) print flight information (by flight_no) (4) select a flight
 * (by source, dest, stop_no = 0) (5) select a flight (by source, dest, stop_no
 * = 1)
 * 
 * @author comp1160/2016
 */
public class FlightManager 
{
	Scanner in = null;
	Connection conn = null;
	// Database Host
	final String databaseHost = "orasrv1.comp.hkbu.edu.hk";
	// Database Port
	final int databasePort = 1521;
	// Database name
	final String database = "pdborcl.orasrv1.comp.hkbu.edu.hk";
	final String proxyHost = "faith.comp.hkbu.edu.hk";
	final int proxyPort = 22;
	final String forwardHost = "localhost";
	int forwardPort;
	Session proxySession = null;
	boolean noException = true;
	// JDBC connecting host
	String jdbcHost;
	// JDBC connecting port
	int jdbcPort;
	String[] options = { // if you want to add an option, append to the end of
							// this array
			"add a flight", "print flight information (by flight_no)", "delete a flight (by flight_no)",
			"select a flight (by source, dest, stop_no = 0)", "select a flight (by source, dest, stop_no = 1)",
			"exit" };
	/**
	 * Get YES or NO. Do not change this function.
	 * 
	 * @return boolean
	 */
	boolean getYESorNO(String message) 
	{
		JPanel panel = new JPanel();
		panel.add(new JLabel(message));
		JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
		JDialog dialog = pane.createDialog(null, "Question");
		dialog.setVisible(true);
		boolean result = JOptionPane.YES_OPTION == (int) pane.getValue();
		dialog.dispose();
		return result;
	}
	/**
	 * Get username & password. Do not change this function.
	 * 
	 * @return username & password
	 */
	String[] getUsernamePassword(String title) 
	{
		JPanel panel = new JPanel();
		final TextField usernameField = new TextField();
		final JPasswordField passwordField = new JPasswordField();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(new JLabel("Username"));
		panel.add(usernameField);
		panel.add(new JLabel("Password"));
		panel.add(passwordField);
		JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) 
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void selectInitialValue() 
			{
				usernameField.requestFocusInWindow();
			}
		};
		JDialog dialog = pane.createDialog(null, title);
		dialog.setVisible(true);
		dialog.dispose();
		return new String[] 
				{ usernameField.getText(), new String(passwordField.getPassword()) };
	}
	/**
	 * Login the proxy. Do not change this function.
	 * 
	 * @return boolean
	 */
	public boolean loginProxy() 
	{
		if (getYESorNO("Using ssh tunnel or not?")) 
		{ // if using ssh tunnel
			String[] namePwd = getUsernamePassword("Login cs lab computer");
			String sshUser = namePwd[0];
			String sshPwd = namePwd[1];
			try 
			{
				proxySession = new JSch().getSession(sshUser, proxyHost, proxyPort);
				proxySession.setPassword(sshPwd);
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				proxySession.setConfig(config);
				proxySession.connect();
				proxySession.setPortForwardingL(forwardHost, 0, databaseHost, databasePort);
				forwardPort = Integer.parseInt(proxySession.getPortForwardingL()[0].split(":")[0]);
			} 
			catch (JSchException e) 
			{
				e.printStackTrace();
				return false;
			}
			jdbcHost = forwardHost;
			jdbcPort = forwardPort;
		} 
		else 
		{
			jdbcHost = databaseHost;
			jdbcPort = databasePort;
		}
		return true;
	}
	/**
	 * Login the oracle system. Do not change this function.
	 * 
	 * @return boolean
	 */
	public boolean loginDB() 
	{
		String[] namePwd = getUsernamePassword("Login sqlplus");
		String username = namePwd[0];
		String password = namePwd[1];
		String URL = "jdbc:oracle:thin:@" + jdbcHost + ":" + jdbcPort + "/" + database;

		try 
		{
			System.out.println("Logging " + URL + " ...");
			conn = DriverManager.getConnection(URL, username, password);
			return true;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Show the options. If you want to add one more option, put into the
	 * options array above.
	 */
	public void showOptions() 
	{
		System.out.println("Please choose following option:");
		for (int i = 0; i < options.length; ++i) 
		{
			System.out.println("(" + (i + 1) + ") " + options[i]);
		}
	}

	/**
	 * Run the manager
	 */
	public void run() 
	{
		while (noException) 
		{
			showOptions();
			String line = in.nextLine();
			if (line.equalsIgnoreCase("exit"))
				return;
			int choice = -1;
			try 
			{
				choice = Integer.parseInt(line);
			} 
			catch (Exception e) 
			{
				System.out.println("This option is not available");
				continue;
			}
			if (!(choice >= 1 && choice <= options.length)) 
			{
				System.out.println("This option is not available");
				continue;
			}
			if (options[choice - 1].equals("add a flight")) 
			{
				addFlight();
			} 
			else if (options[choice - 1].equals("delete a flight (by flight_no)")) 
			{
				deleteFlight();
			} 
			else if (options[choice - 1].equals("print flight information (by flight_no)")) 
			{
				printFlightByNo();
			} 
			else if (options[choice - 1].equals("select a flight (by source, dest, stop_no = 0)")) 
			{
				selectFlightsInZeroStop();
			} 
			else if (options[choice - 1].equals("select a flight (by source, dest, stop_no = 1)")) 
			{
				selectFlightsInOneStop();
			}
			else if (options[choice - 1].equals("exit")) 
			{
				break;
			}
		}
	}
	/**
	 * Print out the infomation of a flight given a flight_no
	 * 
	 * @param flight_no
	 */
	private void printFlightInfo(String flight_no) 
	{
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "SELECT * FROM FLIGHTS WHERE Flight_no = '" + flight_no + "'";
			ResultSet rs = stm.executeQuery(sql);
			if (!rs.next())
				return;
			String[] heads = { "Flight_no", "Depart_Time", "Arrive_Time", "Fare", "Source", "Dest" };
			for (int i = 0; i < 6; ++i) { // flight table 6 attributes
				try 
				{
					System.out.println(heads[i] + " : " + rs.getString(i + 1));
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
		} 
		catch (SQLException e1) 
		{
			e1.printStackTrace();
			noException = false;
		}
	}

	/**
	 * List all flights in the database.
	 */
	private void listAllFlights() 
	{
		System.out.println("All flights in the database now:");
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "SELECT Flight_no FROM FLIGHTS";
			ResultSet rs = stm.executeQuery(sql);

			int resultCount = 0;
			while (rs.next()) 
			{
				System.out.println(rs.getString(1));
				++resultCount;
			}
			System.out.println("Total " + resultCount + " flight(s).");
			rs.close();
			stm.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			noException = false;
		}
	}
	/**
	 * Select out a flight according to the flight_no.
	 */
	private void printFlightByNo() 
	{
		listAllFlights();
		System.out.println("Please input the flight_no to print info:");
		String line = in.nextLine();
		line = line.trim();
		if (line.equalsIgnoreCase("exit"))
			return;
		printFlightInfo(line);
	}

	/**
	 * Given source and dest, select all the flights can arrive the dest
	 * directly. For example, given HK, Tokyo, you may find HK -> Tokyo Your job
	 * to fill in this function.
	 */
	private void selectFlightsInZeroStop() 
	{
		System.out.println("Please input source, dest:");
		String line = in.nextLine();
		if (line.equalsIgnoreCase("exit"))
			return;
		String[] values = line.split(",");
		for (int i = 0; i < values.length; ++i)
			values[i] = values[i].trim();
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "";
			sql = "SELECT flight_no FROM FLIGHTS WHERE SOURCE='" 
					+ values[0] + "'" + "AND DEST='"+ values[1]+"'"
					+" GROUP BY flight_no HAVING COUNT(*) = 1";
			System.out.println(sql);
			ResultSet rs = stm.executeQuery(sql);
			int resultCount = 0; // a counter to count the number of result
			while (rs.next()) 
			{ 
				printFlightInfo(rs.getString(1));
				System.out.println("==============================");
				resultCount ++;
			}
			System.out.println("Total " + resultCount + " choice(s).");
			rs.close();
			stm.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			noException = false;
		}
	}
	/**
	 * Given source and dest, select all the flights can arrive the dest in one
	 * stop. For example, given HK, Tokyo, you may find HK -> Beijing, Beijing
	 * -> Tokyo Your job to fill in this function.
	 */
	private void selectFlightsInOneStop() 
	{
		System.out.println("Please input source, dest:");
		String line = in.nextLine();
		if (line.equalsIgnoreCase("exit"))
			return;
		String[] values = line.split(",");
		for (int i = 0; i < values.length; ++i)
			values[i] = values[i].trim();
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "";
			sql = "SELECT f1.flight_no, f2.flight_no "
					+ "FROM FLIGHTS f1, FLIGHTS f2 "
					+ "WHERE f1.dest = f2.source AND "
					+ "f1.source ='" + values[0] +"' AND "
					+ "f2.dest ='"+ values[1] + "' AND "
					+ "f1.ARRIVE_TIME < f2.DEPART_TIME";
			System.out.println(sql);
			ResultSet rs = stm.executeQuery(sql);
			int resultCount = 0; // a counter to count the number of result
			while (rs.next()) 
			{ 
				printFlightInfo(rs.getString(1));
				System.out.println("-------------------------------------------------");
				printFlightInfo(rs.getString(2));
				resultCount ++;
				System.out.println("==============================");
			}
			System.out.println("Total " + resultCount + " choice(s).");
			rs.close();
			stm.close();
		} 
		catch (SQLException e) 
		{ 
			e.printStackTrace(); noException = false;
		}
	}
	/**
	 * Insert data into database
	 * 
	 * @return
	 */
	private void addFlight() 
	{
		/**
		 * A sample input is: CX109, 2015/03/15/13:00:00, 2015/03/15/19:00:00,
		 * 2000, Beijing, Tokyo
		 */
		System.out.println("Please input the flight_no, depart_time, arrive_time, fare, source, dest:");
		String line = in.nextLine();
		if (line.equalsIgnoreCase("exit"))
			return;
		String[] values = line.split(",");
		if (values.length < 6) 
		{
			System.out.println("The value number is expected to be 6");
			return;
		}
		for (int i = 0; i < values.length; ++i)
			values[i] = values[i].trim();
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "INSERT INTO FLIGHTS VALUES(" + "'" + values[0] + "', " + // this
																					// is
																					// flight
																					// no
			"to_date('" + values[1] + "', 'yyyy/mm/dd/hh24:mi:ss'), " + // this
																		// is
																		// depart_time
			"to_date('" + values[2] + "', 'yyyy/mm/dd/hh24:mi:ss'), " + // this
																		// is
																		// arrive_time
			values[3] + ", " + // this is fare
					"'" + values[4] + "', " + // this is source
					"'" + values[5] + "'" + // this is dest
					")";
			stm.executeUpdate(sql);
			stm.close();
			System.out.println("succeed to add flight ");
			printFlightInfo(values[0]);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("fail to add a flight " + line);
			noException = false;
		}
	}
	/**
	 * Please fill in this function to delete a flight.
	 */
	public void deleteFlight() 
	{
		listAllFlights();
		System.out.println("Please input the flight_no to delete:");
		String line = in.nextLine();
		if (line.equalsIgnoreCase("exit"))
			return;
		line = line.trim();
		try 
		{
			Statement stm = conn.createStatement();
			String sql;
			sql = "DELETE FROM flights WHERE Flight_no='"+line +"'";
			stm.executeUpdate(sql); // please pay attention that we use
									// executeUpdate to update the database
			stm.close();
			System.out.println("succeed to delete flight " + line);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("fail to delete flight " + line);
			noException = false;
		}
	}
	/**
	 * Close the manager. Do not change this function.
	 */
	public void close() 
	{
		System.out.println("Thanks for using this manager! Bye...");
		try 
		{
			if (conn != null)
				conn.close();
			if (proxySession != null) 
			{
				proxySession.disconnect();
			}
			in.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	/**
	 * Constructor of flight manager Do not change this function.
	 */
	public FlightManager() 
	{
		System.out.println("Welcome to use this manager!");
		in = new Scanner(System.in);
	}
	/**
	 * Main function
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		FlightManager manager = new FlightManager();
		if (!manager.loginProxy()) 
		{
			System.out.println("Login proxy failed, please re-examine your username and password!");
			return;
		}
		if (!manager.loginDB()) 
		{
			System.out.println("Login database failed, please re-examine your username and password!");
			return;
		}
		System.out.println("Login succeed!");
		try 
		{
			manager.run();
		} 
		finally 
		{
			manager.close();
		}
	}
}
