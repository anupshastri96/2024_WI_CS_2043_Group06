import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.ArrayList;



public class JDBC_Search{

    //lets you search books either by title or author name
      public static String[] search(String searchString) throws SQLException{

        try{
        Connection connector = DriverManager.getConnection
              ("jdbc:mysql://cs1103.cs.unb.ca:3306/dbname",  // Database URL
               "userid",   // MySQL username
               "ABCDEF");  // MySQL password


        String search = "select title, name, price from Books natural join Authors where title LIKE '%?%' OR name LIKE '%?%';";
        PreparedStatement searchStatement = connector.prepareStatement(search);
        searchStatement.setString(1, searchString);
        searchStatement.setString(2, searchString);
        ResultSet results = searchStatement.executeQuery();

            ArrayList<String> resultsArray = new ArrayList<String>();
            String x = "";
            int counter = 0;
            while(results.next()){
                x+="Title: " + results.getString("title") +
                    "\nAuthor: " + results.getString("name") +
                    "\nPrice: ";
                x+= results.getDouble("price") + "$";
                resultsArray.add(x);
                counter++;
                
            }
            String[] output = new String[counter];
            for (int i = 0; i < counter; i++) {
                output[i] = resultsArray.get(i);
            }
            return output;
        }
        catch (SQLException e){
         System.out.println("SQL error: " + e.getMessage());
         return null;
        }
}
}
