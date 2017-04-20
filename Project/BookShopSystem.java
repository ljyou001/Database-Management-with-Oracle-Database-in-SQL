import java.awt.GridLayout;
import java.awt.TextField;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import javax.swing.*;

import java.util.Calendar;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class BookshopSystem {

	int oid = 100;
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
			"Search an order", "Create an order", "Cancel an order" };

	/**
	 * Get YES or NO. Do not change this function.
	 * 
	 * @return boolean
	 */
	boolean getYESorNO(String message) {
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
	String[] getUsernamePassword(String title) {
		JPanel panel = new JPanel();
		final TextField usernameField = new TextField();
		final JPasswordField passwordField = new JPasswordField();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(new JLabel("Username"));
		panel.add(usernameField);
		panel.add(new JLabel("Password"));
		panel.add(passwordField);
		JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
			private static final long serialVersionUID = 1L;

			@Override
			public void selectInitialValue() {
				usernameField.requestFocusInWindow();
			}
		};
		JDialog dialog = pane.createDialog(null, title);
		dialog.setVisible(true);
		dialog.dispose();
		return new String[] { usernameField.getText(), new String(passwordField.getPassword()) };
	}

	/**
	 * Login the proxy. Do not change this function.
	 * 
	 * @return boolean
	 */
	public boolean loginProxy() {
		if (getYESorNO("Using ssh tunnel or not?")) { // if using ssh tunnel
			String[] namePwd = getUsernamePassword("Login cs lab computer");
			String sshUser = namePwd[0];
			String sshPwd = namePwd[1];
			try {
				proxySession = new JSch().getSession(sshUser, proxyHost, proxyPort);
				proxySession.setPassword(sshPwd);
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				proxySession.setConfig(config);
				proxySession.connect();
				proxySession.setPortForwardingL(forwardHost, 0, databaseHost, databasePort);
				forwardPort = Integer.parseInt(proxySession.getPortForwardingL()[0].split(":")[0]);
			} catch (JSchException e) {
				e.printStackTrace();
				return false;
			}
			jdbcHost = forwardHost;
			jdbcPort = forwardPort;
		} else {
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
	public boolean loginDB() {
		String[] namePwd = getUsernamePassword("Login sqlplus");
		String username = namePwd[0];
		String password = namePwd[1];
		String URL = "jdbc:oracle:thin:@" + jdbcHost + ":" + jdbcPort + "/" + database;

		try {
			System.out.println("Logging " + URL + " ...");
			conn = DriverManager.getConnection(URL, username, password);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Show the options. If you want to add one more option, put into the
	 * options array above.
	 */
	public void showOptions() {
		System.out.println("Please choose following option:");
		for (int i = 0; i < options.length; ++i) {
			System.out.println("(" + (i + 1) + ") " + options[i]);
		}
	}

	public void run() {
		while (noException) {
			showOptions();
			String line = in.nextLine();
			if (line.equalsIgnoreCase("exit"))
				return;
			int choice = -1;
			try {
				choice = Integer.parseInt(line);
			} catch (Exception e) {
				System.out.println("This option is not available");
				continue;
			}
			if (!(choice >= 1 && choice <= options.length)) {
				System.out.println("This option is not available");
				continue;
			}
			if (options[choice - 1].equals("Search an order")) {
				SearchOrder();
			} else if (options[choice - 1].equals("Create an order")) {
				MakeOrder();
			} else if (options[choice - 1].equals("Cancel an order")) {
				CancelOrder();
			} else if (options[choice - 1].equals("exit")) {
				break;
			}
		}
	}

	public BookshopSystem() {

		System.out.println("Welcome to use this University Bookshop System!");
		in = new Scanner(System.in);
	}

	public void close() {
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

	Date a;

	// Given a student number, display all orders made by him/her
	public void SearchOrder() {

		System.out.println("Please input the student number:");
		int stunum = in.nextInt();
		try {

			Statement stm = conn.createStatement();
			String sql = "";

			sql = "SELECT * FROM ORDERRECORD R, ORDERDETAIL D WHERE SID=" + stunum + "AND R.OID=D.OID";

			System.out.println(sql);
			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {

				System.out.println(rs);

			}

			rs.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
			noException = false;
		}

	}

	public void MakeOrder() {
		oid++;
		int cardno = 0;
		boolean creditcard = false;
		boolean outOfstock = false;
		boolean orderoutstanding = false;

		// get the order information from user
		System.out.println("Please input the student number:");
		int stunum = in.nextInt();
		in.nextLine();

		// get the order date
		System.out.println("Please input the date(DD/MM/YYYY):");
		String date = in.nextLine();
		in.nextLine();
		a = Date.valueOf(date);

		System.out.println("Please input the total price:");
		int tprice = in.nextInt();
		in.nextLine();
		System.out.println("Please input the book ids(separate by ,):");
		String[] bookid = in.nextLine().split(",");
		in.nextLine();

		System.out.println("Please input the payment method:");
		String pmethod = in.nextLine();
		in.nextLine();
		if (pmethod.contains("credit card")) {
			creditcard = true;
			System.out.println("Please input the card number:");
			cardno = in.nextInt();
			in.nextLine();
		}

		try {

			Statement stm = conn.createStatement();
			String sql = "";

			// check if all the books are not out of stock
			sql = "SELECT AMOUNT FROM ORDERRECORD R, ORDERDETAIL D, BOOK B WHERE SID=" + stunum
					+ "AND R.OID=D.OID AND D.BN=B.BN";

			System.out.println(sql);
			ResultSet rs1 = stm.executeQuery(sql);

			while (rs1.next()) {
				if (rs1.getInt(1) <= 0) {
					outOfstock = true;
					break;
				}
				System.out.println(rs1);
			}

			// check all there is no other outstanding orders
			sql = "SELECT oid FROM ORDERRECORD WHERE SID=" + stunum;

			System.out.println(sql);
			ResultSet rs2 = stm.executeQuery(sql);

			if (rs2.next()) {
				orderoutstanding = true;
			}

			// create order after checking
			if (outOfstock == true && orderoutstanding == true) {
				if (creditcard == true) {
					for (int i = 0; i < bookid.length; i++) {

						sql = "INSERT INTO ORDERRECORD VALUES(" + oid + "," + stunum + "," + date + "," + tprice + ","
								+ pmethod + "," + cardno + ")" + "INSERT INTO ORDERDETAIL VALUES(" + oid + ","
								+ bookid[i] + ")";
					}

				} else {
					for (int i = 0; i < bookid.length; i++) {

						sql = "INSERT INTO ORDERRECORD VALUES(" + oid + "," + stunum + "," + date + "," + tprice + ","
								+ pmethod + ")" + "INSERT INTO ORDERDETAIL VALUES(" + oid + "," + bookid[i] + ")";
					}
				}

			} else {
				System.out.println("Order Created Fail!");

			}

			System.out.println("Order Created Success!");
			System.out.println(sql);

			rs1.close();
			rs2.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
			noException = false;
		}

	}

	public void CancelOrder() {
		boolean delivered = false;
		boolean sDays = false;

		System.out.println("Please input the order id needed to be cancel:");
		int oid = in.nextInt();
		in.nextLine();

		try {

			Statement stm = conn.createStatement();
			String sql = "";

			// check if there is book already delivered 如果是不检测的话 需要备注掉这里
			sql = "SELECT deliver FROM ORDERDETAIL WHERE oid=" + oid;
			System.out.println(sql);
			ResultSet rs1 = stm.executeQuery(sql);

			while (rs1.next()) {
				if (rs1.getInt(1) == 1) {
					delivered = true;
				}
			}

			// check if the order made within 7 days
			sql = "SELECT date FROM ORDERRECORD WHERE oid=" + oid;
			System.out.println(sql);
			ResultSet rs2 = stm.executeQuery(sql);

			// get the date of today
			SimpleDateFormat sdf = new SimpleDateFormat("DD/MM/YYYY");
			String datetime = sdf.format(new java.util.Date());
			Date b = Date.valueOf(datetime);

			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();

			while (rs2.next()) {

				c1.setTime(a);
				c2.setTime(b);

				if (c2.compareTo(c1) > 7) {
					sDays = true;
					break;
				}

			}

			if (delivered == false && sDays == false) {
				sql = "DELETE FROM ORDERRECORD WHERE oid=" + oid;
				System.out.println(sql);
				System.out.println("Order canceled success!");
			} else {
				System.out.println("Order canceled fail!");
			}

			// the version that do not check deliver or not in this program
			// 不检测的话 只需要判断sDay那一段就可以了，不确定trigger的错误信息会不会返回到java，需要测试
			/*
			 * if (sDays == false) { sql = "DELETE FROM ORDERRECORD WHERE oid="
			 * + oid; System.out.println(sql);
			 * System.out.println("Order canceled success!"); } else {
			 * System.out.println("Order canceled fail!"); }
			 */

			rs1.close();
			rs2.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
			noException = false;
		}

	}

	public static void main(String[] args) {

		BookshopSystem system = new BookshopSystem();
		if (!system.loginProxy()) {
			System.out.println("Login proxy failed, please re-examine your username and password!");
			return;
		}
		if (!system.loginDB()) {
			System.out.println("Login database failed, please re-examine your username and password!");
			return;
		}
		System.out.println("Login succeed!");
		try {
			system.run();
		} finally {
			system.close();
		}
	}
}
