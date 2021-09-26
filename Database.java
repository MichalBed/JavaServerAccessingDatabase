import java.sql.*;
import java.util.ArrayList;

public class Database {
	private Connection con;
	
	public Connection getCon() {
		return con;
	}
	
	public void setCon(Connection con) {
		this.con = con;
	}
	
	
	// Returns all USER IDS from MessageUser table
	public ArrayList<String> getUserIDs(){
		ArrayList<String> userIDs = new ArrayList<String>();
		String query = "SELECT usid FROM messageuser;";
		ResultSet resultSet = executeThisQuery(query);
		try { 
			 while (resultSet.next()) {
				 userIDs.add(resultSet.getString("usid"));
			 }
		 } catch (SQLException e) {
			 e.printStackTrace();
		 }
		return userIDs;
	}
	
	
	// Returns user IDs of users in the MessageGroup "groupid"
	public ArrayList<String> getGroupUserIDs(int groupid){
		ArrayList<String> userIDs = new ArrayList<String>();
		try { 
			PreparedStatement query = con.prepareStatement("SELECT usid FROM membership WHERE groupid = ?;");
			query.setInt(1, groupid);
			ResultSet resultSet = query.executeQuery();
			 while (resultSet.next()) {
				 userIDs.add(resultSet.getString("usid").toString());
			 }
		 } catch (SQLException e) {
			 e.printStackTrace();
		 }
		return userIDs;
	}
	
	
	// List of <displayname, message text> for all messages in the group
	public ArrayList<ArrayList<String>> getGroupMessages(String groupName){
		ArrayList<ArrayList<String>> messagesList = new ArrayList<ArrayList<String>>();
		try {
			PreparedStatement query = con.prepareStatement("SELECT displayname, messagetext FROM message NATURAL JOIN messageuser NATURAL JOIN messagegroup WHERE groupname = ?");
			query.setString(1,groupName);
			ResultSet resultSet = query.executeQuery();
			
			while (resultSet.next()) {
				ArrayList<String> r = new ArrayList<String>();
				r.add(resultSet.getObject("displayname").toString());
				r.add(resultSet.getObject("messagetext").toString());
				messagesList.add(r);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return messagesList;
	}

	
	// Displayname, groupname and messagetext for all messages posted to all groups by user
	public ArrayList<ArrayList<String>> getAllMessagesByUser(int usid){
		ArrayList<ArrayList<String>> messagesList = new ArrayList<ArrayList<String>>();

		try {
			PreparedStatement query = con.prepareStatement("SELECT displayname, groupname, messagetext FROM messageuser NATURAL JOIN message NATURAL JOIN messagegroup WHERE messageuser.usid = ?");
			query.setInt(1, usid);
			
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				ArrayList<String> r = new ArrayList<String>();
				r.add(rs.getObject("displayname").toString());
				r.add(rs.getObject("groupname").toString());
				r.add(rs.getObject("messagetext").toString());
				messagesList.add(r);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messagesList;
	}	
	
	
	// Adds a new user with a display name and phone number
	public void addUser(String displayName, int phoneNumber) {
		int nextID = getNextUserID();
		
		try {
			PreparedStatement query = con.prepareStatement("INSERT INTO messageuser (values(?,?,?))");
			
			query.setInt(1,nextID);
			query.setInt(2,phoneNumber);
			query.setString(3,displayName);
			
			query.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	// Adds a user to a specified group
	public void addUserToGroup(int usid, String groupname) {
		try {
			PreparedStatement stmt = con.prepareStatement("INSERT INTO membership values ((SELECT groupid FROM messagegroup WHERE groupname = ?),?)");
		
			stmt.setString(1,groupname);
			stmt.setInt(2,usid);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Adds a new group with name groupName
	public void addGroup(String groupName) {		
		int newid = getNextGroupID();
		
		try {	
			PreparedStatement stmt = con.prepareStatement("insert into messagegroup values(?,?)");
			
			stmt.setInt(1, newid);
			stmt.setString(2, groupName);
		
			stmt.executeUpdate();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Adds a new message by user to group with the context text
	public void addMessage(int usid, int groupid, String text) {
		int nextid = getNextMessageID();
		
		try {			
			PreparedStatement stmt = con.prepareStatement("INSERT INTO message values(?,?,?,?)");
			
			stmt.setInt(1, nextid);
			stmt.setInt(2, usid);
			stmt.setInt(3, groupid);
			stmt.setString(4, text);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	// Returns next available userID for User 
	private int getNextUserID() {
		String query = "SELECT max(usid) FROM messageuser";
		int maxID = 0;
		
		try {
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				maxID = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (maxID < 0) {
			return maxID;
		}
		return maxID + 1;
	}

	
	// Returns next available message ID for message
	private int getNextMessageID() {
		String query = "SELECT max(messageid) FROM message";
		int maxid = 0;
		
		try {
			PreparedStatement stmt = con.prepareStatement(query);	
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				maxid = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (maxid < 0) {
			return maxid;
		}
		return maxid + 1;
	}
	
	
	// Returns next available group ID for group
	private int getNextGroupID() {
		String query = "SELECT max(groupid) FROM messagegroup";
		int maxid = 0;
		
		try {
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				maxid = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (maxid < 0) {
			return maxid;
		}
		return maxid + 1;
	}


	
	// Prints an ArrayList of ArrayList<String>
	private static void print2(ArrayList<ArrayList<String>> list) {
		for (ArrayList<String> s: list) {
			print1(s);
			System.out.println();
		}
	}
		
	// Prints an ArrayList
	private static void print1(ArrayList<String> list) {
		for (String s: list) {
			System.out.print(s + " ");
		}
	}

	
	
	// Executes a query and returns a ResultSet
	public ResultSet executeThisQuery(String query) {
		ResultSet rset = null;
		try {
			Statement stmt = con.createStatement();
			rset = stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rset;
	}
	
	
	// Establishes a DB connection and returns a boolean	
	public boolean establishDBConnection() {
		String USER_Name = "postgres";
		String PASSWORD = "password";
		
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql" 
			+ "://localhost:5432/postgres",USER_Name, PASSWORD);
			return con.isValid(2); // 2 second timeout
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
