package Crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;
import java.net.URL;
import java.io.*;
import java.io.IOException;


public class CrawlerDB {
    static final String DB_URL = "jdbc:mysql://localhost:3306/apt";
    static final String USER = "root";
    static final String PASS = "123456";
    static Connection conn;

    public class linkAndID {
        public String link;
        public int id;
        public linkAndID(String link, int id){
            this.link = link;
            this.id = id;
        }
    }

    public CrawlerDB(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
    }
    
    public void createTable() {
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

    public void dropTable(){
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE crawler";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
    }
   
    public void addLink(String link){
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
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
   
    public void markVisitedLink(String link){
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "UPDATE crawler SET visited = ? WHERE link = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setString(2, link);
            pstmt.executeUpdate();
            System.out.println("Link update");
        }catch(SQLException e){e.printStackTrace();}
    }
    
    public void markIndexedLink(String link){
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "UPDATE crawler SET indexed = ? WHERE link = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setString(2, link);
            pstmt.executeUpdate();
            System.out.println("Link update");
        }catch(SQLException e){e.printStackTrace();}
    }
   
    public boolean checkLink(String link){
        boolean canAddToDB = false;
        try{
            String sql = "SELECT * FROM crawler WHERE link= ?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, link); 
            ResultSet result = pstmt.executeQuery();
            if(result.next() == false){
                //check if downloadable link
                URL url = new URL(link);
                new BufferedReader(new InputStreamReader(url.openStream()));

                canAddToDB = true;
                System.out.println("Valid");
            }
            else{
                System.out.println("Invalid");
            }
        }
        catch(SQLException e){e.printStackTrace();}
        catch (IOException io) {
            System.out.println("Error");
        }
        return canAddToDB;
    }
    
    public Queue<linkAndID> getLinksToVisit(int numberOfReqLinks){
        Queue<linkAndID> queue = new LinkedList<>();
        try{
            String sql = "SELECT * FROM crawler WHERE visited = ? LIMIT ?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, false); 
            pstmt.setInt(2, numberOfReqLinks); 
            ResultSet result = pstmt.executeQuery();
            int i=0;
            while(result.next()){
                i++;
                System.out.println("Number in queue: "+i);
                int id = result.getInt(1);
                String linkToVisit = result.getString(2);
                linkAndID temp = new linkAndID(linkToVisit,id);
                queue.add(temp);
            }
        }
        catch(SQLException e){e.printStackTrace();}
        return queue;
    }
    
    public int getTotalNumberOfLinks(){
        try{
            String sql = "SELECT COUNT(id) AS count FROM crawler";   
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()){
                return result.getInt("count");
            } 
        }
        catch(SQLException e){e.printStackTrace();}
        return -1;
    }
    
    public int getTotalNumberOfDownloadedLinks(){

        try{
            String sql = "SELECT COUNT(id) AS count FROM crawler WHERE visited=true";   
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()){
                return result.getInt("count");
            } 
        }
        catch(SQLException e){e.printStackTrace();}
        return -1;
    }

    public static void main(String[] args) {
        //CrawlerDB DB = new CrawlerDB();
        
        //dropTable();
        //createTable();
        //addLink("google4.com");
        //markVisitedLink("google2.com");
        //markIndexedLink("google2.com");
        //checkLink("google2.com");//return bool
        //Queue<String> queue = getLinksToVisit();
        //System.out.println(DB.getTotalNumberOfLinks());
    }
}
