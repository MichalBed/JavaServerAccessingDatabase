import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Server {

	private static Database DATABASE = null;
	
	public static void main(String args[]) {
		System.out.println("Server is running \n Now connecting to Database");
		
		//Connect to Database
		DATABASE = new Database();
		if (!DATABASE.establishDBConnection()) {
			// Connection failed
			System.out.println("Database connection fail");
		} else
			// Connection successful
			System.out.println("Server is now connected to database");
			
		
			
			ArrayList<String> list = DATABASE.getGroupMessages("falloutgamers");
			
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i));
			}
			
	}
	
	
	
	
}
