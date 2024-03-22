import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class removeBook{
      public static void remove(int bookId) throws SQLException{

        try{
        Connection connector = DriverManager.getConnection
              ("jdbc:mysql://cs1103.cs.unb.ca:3306/dbname",  // Database URL
               "userid",   // MySQL username
               "ABCDEF");  // MySQL password


        String delete = "delete from Books where book_id = ?;";
        PreparedStatement deleteStatement = connector.prepareStatement(delete);
        deleteStatement.setInt(1, bookId);

        int affectedRows = deleteStatement.executeUpdate();
              if (affectedRows == 0){
                  System.out.println("\nRemoval failed\n");
             }
              else{
                  System.out.println("\nRemoval succeeded\n");
             }
        }
        catch (SQLException e){
         System.out.println("SQL error: " + e.getMessage);
        }
}
}
