package DBManager;

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

public class DBManager {
    static final String DB_URL = "jdbc:mysql://localhost:3306/apt";
    static final String USER = "root";
    static final String PASS = "admin12345";
    static Connection conn;

    public DBManager(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
    }
   
    public void createTables() {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE WORD " + 
                         "(id INTEGER not NULL AUTO_INCREMENT, " + 
                         " Name VARCHAR(255), "+ 
                         " IDF FLOAT,"+
                         "PRIMARY KEY (id))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
        }catch(SQLException e){e.printStackTrace();}

        try{
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE LINKS " + 
                         "(id INTEGER not NULL AUTO_INCREMENT, " + 
                         " URL VARCHAR(255) not NULL, "+ 
                         " TF INTEGER,"+
                         " Plain INTEGER,"+
                         " Header INTEGER,"+
                         " Title INTEGER,"+
                         " Word_id INTEGER not NULL,"+
                         "FOREIGN KEY (Word_id) references WORD(id),"+
                         "CONSTRAINT URL_id UNIQUE (Word_id,URL),"+
                         "PRIMARY KEY (id))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
        }catch(SQLException e){e.printStackTrace();}
        
    }

    public void dropTables(){
        try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE LINKS";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
        try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE WORD";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
    }
   
    /******************************************************* Crawler Table  *********************************************************/
    public int getAllLinksCount_CrawlerTable(){
        try{
            String sql = "SELECT COUNT(id) AS count FROM crawler WHERE visited=true";   
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()){
                return result.getInt("count");
            } 
        }
        catch(SQLException e){
            //e.printStackTrace();
        }
        return -1;
    }

    public String getLinkFromID_CrawlerTable(int id){
        try{
            String sql = "SELECT link AS link FROM crawler Where id = ?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id); 
            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                return result.getString(1);
            } 
        }
        catch(SQLException e){
            //e.printStackTrace();
        }
        return "";
    }

    public Queue<Integer> getLinksToVisit_CrawlerTable(int numberOfReqLinks){
        Queue<Integer> queue = new LinkedList<>();
        try{
            String sql = "SELECT id FROM crawler WHERE indexed =? AND visited =? LIMIT ?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, false); 
            pstmt.setBoolean(2, true); 
            pstmt.setInt(3, numberOfReqLinks); 
            ResultSet result = pstmt.executeQuery();
            //int i=0;
            while(result.next()){
                //i++;
                //System.out.println("Number in queue: "+i);
                int id = result.getInt(1);
                queue.add(id);
            }

        }
        catch(SQLException e){
            //e.printStackTrace();
        }
        return queue;
    }

    public void markIndexedLink_CrawlerTable(int id){
        try{
            String sql = "UPDATE crawler SET indexed = ? WHERE id = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            //System.out.println("Link update");
        }catch(SQLException e){
            //e.printStackTrace();
        }
    }
    /******************************************************* Links Table  ***********************************************************/
    public int getTotalNumberOfBatchedLinks(){

        try{
            String sql = "SELECT COUNT(id) AS count FROM crawler WHERE batched=true";   
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()){
                return result.getInt("count");
            } 
        }
        catch(SQLException e){
            //e.printStackTrace();
        }
        return -1;
    }
    
    
    public int getAllLinksCountByID_LinksTable(int id){
        try{
            String sql = "SELECT COUNT(id) AS count FROM links where word_id = ?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id); 
            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                return result.getInt("count");
            } 
        }
        catch(SQLException e){
            //e.printStackTrace();
        }
        return -1;
    }

    
    /******************************************************* Words Table  ***********************************************************/
    
    public int getWordID_WordsTable(String word){
        try{
            String sql = "SELECT id FROM word where Name = ?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, word); 
            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                return result.getInt(1);
            } 
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public void addWord_WordsTable(String word){
        try{
            String sql = "INSERT INTO word " +
                         "(Name)"+
                         "VALUES(?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, word);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void addIDF_WordsTable(int id, float idf){
        try{
            String sql = "UPDATE word SET" +
                         " IDF = ?"+
                         " Where id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setFloat(1, idf);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void addLink_WordsTable(String URL,int TF,int Plain,int Header,int Title,String Name){
		int wordID= getWordID_WordsTable(Name);
		if(wordID == -1){
            addWord_WordsTable(Name);
			wordID = getWordID_WordsTable(Name);
        }
            
        try{
            //System.out.println(wordID);
            String sql = "INSERT INTO links " +
                         "(URL, TF, Plain, Header, Title, Word_id)"+
                         "VALUES(?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, URL);
            pstmt.setInt(2, TF);
            pstmt.setInt(3, Plain);
            pstmt.setInt(4, Header);
            pstmt.setInt(5, Title);
            pstmt.setInt(6, wordID);
            pstmt.executeUpdate();
        }catch(SQLException e){
            //e.printStackTrace();
        }

		int totalDocNum = getAllLinksCount_CrawlerTable();
		int totalDocNUMForWord = getAllLinksCountByID_LinksTable(wordID);
		float IDF= ((float)totalDocNum/(float)totalDocNUMForWord);
        addIDF_WordsTable(wordID, IDF);
	}

    public static void main(String[] args) {
        DBManager DB = new DBManager();
        DB.dropTables();
        DB.createTables();
    }
}
