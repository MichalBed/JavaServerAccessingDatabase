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

	
	//public ArrayList<ArrayList<String>> getAllMessagesByUser(int usid){
		
	//}
	
	//public ArrayList<String> getMostActiveUsers(){
		
	//}
	
	//public ArrayList<String> getInactiveUsers(){
		
	//}
	
	public void addUser(String displayName, int phoneNumber) {
		
	}
	
	public void addGroup(String groupName) {
		
	}
	
	public void addMessage(int usid, int groupid, String text) {
		
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
