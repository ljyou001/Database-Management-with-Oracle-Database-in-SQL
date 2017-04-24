import java.awt.GridLayout;
import java.awt.TextField;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.*;
import java.util.Calendar;
import java.util.Properties;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class BookStoreSystem 
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
	String[] options = 
	{ "Search an order", "Create an order", "Cancel an order", "Record the deliver date of a book","Search order by name" };
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
		return new String[] { usernameField.getText(), new String(passwordField.getPassword()) };
	}
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
	 * Show the options.
	 */
	public void showOptions() 
	{
		System.out.println("Please choose following option:");
		for (int i = 0; i < options.length; ++i) 
		{
			System.out.println("(" + (i + 1) + ") " + options[i]);
		}
	}
	// boolean running=true;
	public void run() throws SQLException 
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
			if (options[choice - 1].equals("Search an order")) 
			{
				SearchOrder();
			} 
			else if (options[choice - 1].equals("Create an order")) 
			{
				MakeOrder();
			} 
			else if (options[choice - 1].equals("Cancel an order")) 
			{
				CancelOrder();
			} 
			else if (options[choice - 1].equals("Record the deliver date of a book")) 
			{
				RecordDate();
			}
			else if (options[choice - 1].equals("Search order by name")) 
			{
				SearchOrderbyStu();
			}
		}
	}
	public BookshopSystem() 
	{
		System.out.println("Welcome to use this University Bookshop System!");
		in = new Scanner(System.in);
	}
	public void close() 
	{
		System.out.println("Thanks for using this system! Bye...");
		try {
			if (conn != null)
				conn.close();
			if (proxySession != null) {
				proxySession.disconnect();
			}
			in.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void SearchOrder() throws SQLException 
	{
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "";
			sql = "SELECT OID FROM ORDERLIST ";
			// System.out.println(sql);
			ResultSet rs = stm.executeQuery(sql);
			System.out.println("All the oids are: ");
			while (rs.next()) 
			{
				System.out.println(rs.getInt(1));
			}
			rs.close();
			stm.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			noException = false;
		}
		System.out.println("Please input the order id:");
		int oid2 = in.nextInt();
		in.nextLine();
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "";
			sql = "SELECT * FROM ORDERLIST L WHERE OID=" + oid2;
			// System.out.println(sql);
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) 
			{
				System.out.println("oid: " + rs.getInt(1));
				System.out.println("card pay: " + rs.getString(2));
				System.out.println("card: " + rs.getInt(3));
				System.out.println("sid:" + rs.getInt(4));
				System.out.println("Orderdate: " + rs.getString(5));
				System.out.println("Total price: " + rs.getDouble(6));
			}
			rs.close();
			stm.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			noException = false;
		}
	}
	// create an order based on user's input
	public void MakeOrder() throws SQLException 
	{
		// the system will automatically give orders an order id
		oid++;
		String cardno = null;
		boolean creditcard = false;
		boolean outOfstock = true;
		boolean orderoutstanding = true;
		// get the order information from user
		System.out.println("Please input the student number:");
		int stunum = in.nextInt();
		in.nextLine();
		// get the order date
		System.out.println("Please input the date(DD/MM/YYYY,hh:mm:ss):");
		String date = in.nextLine();
		System.out.println("Please input the book ids(separate by ,):");
		String[] bookid = in.nextLine().split(",");
		System.out.println("Please input the payment method:");
		int pmethod = in.nextInt();
		in.nextLine();
		if (pmethod == 1) 
		{
			creditcard = true;
			System.out.println("Please input the card number:");
			cardno = in.nextLine();
		}
		if (cardno == "\t") 
		{
			System.out.println("missing credit card no.");
			return;
		} 
		else if (cardno != "\t") 
		{
			try 
			{
				Statement stm = conn.createStatement();
				String sql = "";
				// check if all the books are not out of stock
				for (int i = 0; i < bookid.length; i++) 
				{
					sql = "SELECT AMOUNT FROM BOOK WHERE BN=" + Integer.parseInt(bookid[i]);
					ResultSet rs1 = stm.executeQuery(sql);
					while (rs1.next()) 
					{
						if (rs1.getInt(1) <= 0) 
						{
							outOfstock = false;
							System.out.println(rs1);
							break;
						}
					}
					rs1.close();
				}
				// System.out.println(sql);
				// check all there is no other outstanding orders
				sql = "SELECT oid FROM ORDERLIST WHERE SID=" + stunum;
				// System.out.println(sql);
				ResultSet rs2 = stm.executeQuery(sql);
				if (rs2.next()) 
				{
					orderoutstanding = false;
				}
				rs2.close();
				// System.out.println(outOfstock + "//" + orderoutstanding);
				// create order after checking
				if (outOfstock == true && orderoutstanding == true) 
				{
					if (creditcard == true && cardno != "\t") 
					{
						String sql1 = "INSERT INTO ORDERLIST VALUES(" + oid + "," + pmethod + "," + cardno + ","
								+ stunum + "," + "to_date(\'" + date + "\',\'dd/mm/yyyy,hh24:mi:ss\')" + ",0)";
						stm.executeUpdate(sql1);
						for (int i = 0; i < bookid.length; i++) 
						{
							String sql2 = "INSERT INTO ORDERDETAIL VALUES(" + bookid[i] + "," + oid + "," + null + ")";
							stm.executeUpdate(sql2);
						}
					} 
					else 
					{
						String sql1 = "INSERT INTO ORDERLIST VALUES(" + oid + "," + 0 + "," + null + "," + stunum + ","
								+ "to_date(\'" + date + "\',\'dd/mm/yyyy,hh24:mi:ss\')" + ",0)";
						stm.executeUpdate(sql1);
						for (int i = 0; i < bookid.length; i++) 
						{
							String sql2 = "INSERT INTO ORDERDETAIL VALUES(" + bookid[i] + "," + oid + "," + null + ")";
							stm.executeUpdate(sql2);
						}
					}
					stm.close();
				} 
				else 
				{
					if (outOfstock == false) 
					{
						System.out.println("Order Created Fail! Due to a book is out of stock!");
					} else if (orderoutstanding == false) 
					{
						System.out.println("Order Created Fail ! Due to ther is outstanding order of this student.");
					}
					return;
				}
				// System.out.println(sql);
				System.out.println("Order Created Success!");
				/*
				 * rs1.close(); rs2.close(); stm.close();
				 */
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
				noException = false;
			}
		}
	}
	// cancel an order based on the user's input
	public void CancelOrder() 
	{
		// print out all the order ids
		System.out.println("All the oids are: ");
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "";
			sql = "SELECT OID FROM ORDERLIST ";
			// System.out.println(sql);
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) 
			{
				System.out.println(rs.getInt(1));
			}
			rs.close();
			stm.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			noException = false;
		}
		boolean delivered = false;
		boolean sDays = false;
		System.out.println("Please input the order id needed to be cancel:");
		int oid = in.nextInt();
		in.nextLine();
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "";
			sql = "SELECT sentTime FROM ORDERDETAIL WHERE oid=" + oid;
			//System.out.println(sql);
			ResultSet rs1 = stm.executeQuery(sql);
			while (rs1.next()) 
			{
				if (rs1.getDate(1) != null) 
				{
					delivered = true;
				}
			}
			// check if the order made within 7 days
			sql = "SELECT orderdate FROM ORDERLIST WHERE oid=" + oid;
			ResultSet rs2 = stm.executeQuery(sql);
			while (rs2.next()) 
			{
				a = rs2.getDate(1);
			}
			Calendar now = Calendar.getInstance();
			int month = now.get(Calendar.MONTH) + 1;
			String datetime = now.get(Calendar.YEAR) + "-" + month + "-" + now.get(Calendar.DATE);
			Date b = Date.valueOf(datetime);
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			while (rs2.next()) 
			{
				c1.setTime(a);
				c2.setTime(b);
				if (c2.compareTo(c1) > 7) 
				{
					sDays = true;
					break;
				}

			}
			if (delivered == false && sDays == false) 
			{
				sql = "DELETE FROM ORDERDETAIL WHERE oid=" + oid;
				stm.executeUpdate(sql);
				sql = "DELETE FROM ORDERLIST WHERE oid=" + oid;
				stm.executeUpdate(sql);
				// System.out.println(sql);
				System.out.println("Order canceled success!");
			} 
			else 
			{
				System.out.println("Order canceled fail!");
			}
			rs2.close();
			stm.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			noException = false;
		}
	}

	public static int CEI(String a) 
	{
		int month = 0;
		if (a.equals("JAN")) 
		{
			month = 1;
		} 
		else if (a.equals("FEB")) 
		{
			month = 2;
		} 
		else if (a.equals("MAR")) 
		{
			month = 3;
		} 
		else if (a.equals("APR")) 
		{
			month = 4;
		} 
		else if (a.equals("MAY")) 
		{
			month = 5;
		} 
		else if (a.equals("JUN")) 
		{
			month = 6;
		} 
		else if (a.equals("JUL")) 
		{
			month = 7;
		} 
		else if (a.equals("AUG")) 
		{
			month = 8;
		} 
		else if (a.equals("SEP")) 
		{
			month = 9;
		} 
		else if (a.equals("OCT")) 
		{
			month = 10;
		} 
		else if (a.equals("NOV")) 
		{
			month = 11;
		} 
		else if (a.equals("DEC")) 
		{
			month = 12;
		}
		return month;
	}
	// record the date of delivery of any books(based on users' input)
	private void RecordDate() 
	{
		System.out.println("Please input the order id:");
		int oid = in.nextInt();
		in.nextLine();
		System.out.println("Please input the book ids(separated by , ):");
		String[] bookid = in.nextLine().split(",");
		System.out.println("Please input the date the book was delivered(DD/MM/YYYY,hh:mm:ss):");
		String sentDate = in.nextLine();
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "";
			for (int i = 0; i < bookid.length; i++) 
			{
				sql = "UPDATE ORDERDETAIL SET sentTime = to_date(\'" + sentDate
						+ "\',\'dd/mm/yyyy,hh24:mi:ss\') WHERE oid=" + oid + "AND bn=" + bookid[i];
				stm.executeUpdate(sql);
			}
			System.out.println("Update success!");
			stm.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			noException = false;
		}
	}
	private void SearchOrderbyStu()
	{
		try 
		{
			Statement stm = conn.createStatement();
			String sql = "";
			sql = "SELECT SID,NAME FROM STUDENT ";
			// System.out.println(sql);
			ResultSet rs = stm.executeQuery(sql);
			System.out.println("All the students are: ");
			while (rs.next()) 
			{
				System.out.println("sid: "+rs.getInt(1)+","+"Name: "+rs.getString(2));
			}
			rs.close();
			stm.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			noException = false;
		}
		System.out.println("Please input the sid:");
		int oid2 = in.nextInt();
		in.nextLine();
		try {
			Statement stm = conn.createStatement();
			String sql = "";
			sql = "SELECT * FROM ORDERLIST L WHERE OID=" + oid2;
			// System.out.println(sql);
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) 
			{
				System.out.println("oid: " + rs.getInt(1));
				System.out.println("card pay: " + rs.getString(2));
				System.out.println("card: " + rs.getInt(3));
				System.out.println("sid:" + rs.getInt(4));
				System.out.println("Orderdate: " + rs.getString(5));
				System.out.println("Total price: " + rs.getDouble(6));
			}
			rs.close();
			stm.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			noException = false;
		}
	}
	public static void main(String[] args) throws SQLException 
	{
		BookshopSystem system = new BookshopSystem();
		if (!system.loginProxy()) 
		{
			System.out.println("Login proxy failed, please re-examine your username and password!");
			return;
		}
		if (!system.loginDB()) 
		{
			System.out.println("Login database failed, please re-examine your username and password!");
			return;
		}
		System.out.println("Login succeed!");
		try 
		{
			system.run();
		} 
		finally 
		{
			system.close();
		}
	}
}
