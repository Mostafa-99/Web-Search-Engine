package Crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;

public class CrawlerDB {
    static final String DB_URL = "jdbc:mysql://localhost:3306/apt";
    static final String USER = "root";
    static final String PASS = "123456";
    static Connection conn;
    
    static public void createTable() {
        // Open a connection
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE CRAWLER " + 
                         "(id INTEGER not NULL AUTO_INCREMENT, " + 
                         " link VARCHAR(255) not null, "+ 
                         " visited BOOLEAN not null,"+
                         " indexed BOOLEAN not null,"+
                         "PRIMARY KEY (id))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
        }catch(SQLException e){e.printStackTrace();}
        
    }

    static public void dropTable(){
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE crawler";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
    }
   
    static public void addLink(String link){
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //Statement stmt = conn.createStatement();
            String sql = "INSERT INTO crawler " +
                         "(link,visited,indexed)"+
                         "VALUES(?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, link);
            pstmt.setBoolean(2, false);
            pstmt.setBoolean(3, false);
            pstmt.executeUpdate();
            System.out.println("Link Added to table");
        }catch(SQLException e){e.printStackTrace();}
        
    }
   
    static public void markVisitedLink(String link){
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //Statement stmt = conn.createStatement();
            String sql = "UPDATE crawler SET visited = ? WHERE link = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setString(2, link);
            pstmt.executeUpdate();
            System.out.println("Link update");
        }catch(SQLException e){e.printStackTrace();}
    }
    
    static public void markIndexedLink(String link){
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //Statement stmt = conn.createStatement();
            String sql = "UPDATE crawler SET indexed = ? WHERE link = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setString(2, link);
            pstmt.executeUpdate();
            System.out.println("Link update");
        }catch(SQLException e){e.printStackTrace();}
    }
   
    static public boolean checkLink(String link){
        boolean canAddToDB = false;
        try{
            //Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM crawler WHERE link= ?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, link); 
            ResultSet result = pstmt.executeQuery();
            if(result.next() == false){
                canAddToDB = true;
                System.out.println("Valid");
            }
            else{
                System.out.println("Invalid");
            }
        }
        catch(SQLException e){e.printStackTrace();}
        return canAddToDB;
    }
    
    static public Queue<String> getLinksToVisit(){
        Queue<String> queue = new LinkedList<>();

        try{
            //Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM crawler WHERE visited= ?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, false); 
            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                String linkToVisit = result.getString(2);
                System.out.println(linkToVisit);
                queue.add(linkToVisit);
            }
        }
        catch(SQLException e){e.printStackTrace();}
        return queue;
    }

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
        //createTable();
        //dropTable();
        //addLink("google4.com");
        //markVisitedLink("google2.com");
        //markIndexedLink("google2.com");
        //checkLink("google2.com");//return bool
        Queue<String> queue = getLinksToVisit();
        
    }
}
