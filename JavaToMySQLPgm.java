/**
 *
 * @author Zack Anderson */
import java.sql.*;
import java.security.*;
import java.util.Scanner;

/**
 * Simple Java program to connect to MySQL database running on CS485 server
 * 
 * @author Zack Anderson
 */
 
public class JavaToMySQLPgm {

    // JDBC URL, username and password of MySQL server
    private static final String url = "jdbc:mysql://mrbartucz.com:3306/su5378ow_salthash";
    private static final String user = "su5378ow";
    private static final String password = "Works4me";

    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs1;
    static Scanner console = new Scanner(System.in);

    public static void main(String args[]) throws ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException {
        //  String query = "select Password1 from userID where Userid=1";
        System.out.println("Please enter the user id you would like to change the password (Admin, relator or guest" ); 
        String UserID = console.nextLine();
        System.out.println("Please enter the password you would like to store encrypted:" );    
        String passwordToHash = console.nextLine();
        byte[] salt = getSalt();
        String securePassword = getSecurePassword(passwordToHash, salt);
        //System.out.println(securePassword); 
        
        String query = "update userID set password1 = '"+securePassword+"' where Userid='"+UserID+"'";
   
        try {
          // load and register JDBC driver for MySQL 
          con = DriverManager.getConnection(url, user, password);
             if (con != null) {
           //     System.out.println("Successfully connected to MySQL database");
             }
              
           // getting Statement object to execute query
            stmt = con.createStatement();

            // executing SELECT query
            int  rsint = stmt.executeUpdate(query);
            System.out.printf("User Password Updated.: for user "+UserID+"\n");
                System.out.println();
          // Redo... checking password
           System.out.println("Please enter the password you just entered for userid "+UserID+" to confirm it is correct in Database:" );    
           String passwordfromUser = console.nextLine();
           //salt = getSalt();
           String securePassword1 = getSecurePassword(passwordfromUser, salt);
          
           query = "select password1 from userID where Userid='"+UserID+"'";
         //  System.out.println(query);
           // getting Statement object to execute query
           stmt = con.createStatement();
           // executing SELECT query       
           rs1 = stmt.executeQuery(query);
                      
        while (rs1.next()) {
                String passCode = rs1.getString("password1");
                
                if (securePassword1.equals(passCode))
                { System.out.println("Password matches");  }  
                else 
                 { System.out.println("Failure!  Password does not match");  }  
                
                 System.out.println("Password in the database is:"+ passCode + " You entered "+ passwordfromUser + " which is "+
                 securePassword1);
            }

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs1.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }
    
    
      private static String getSecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes 
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    
    //Add salt
    private static byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt;
    }
}


