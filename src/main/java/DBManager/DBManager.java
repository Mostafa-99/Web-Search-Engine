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
    static final String PASS = "Mohammed.33";
    static Connection conn;
    
    public class linkAndID {
        public String link;
        public int id;
        public linkAndID(String link, int id){
            this.link = link;
            this.id = id;
        }
    }
    
    public DBManager(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
    }
   
    public void createTables() {
        try{
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE CRAWLER " + 
                         "(id INTEGER not NULL AUTO_INCREMENT, " + 
                         " link VARCHAR(255) not null, "+ 
                         " visited BOOLEAN not null,"+
                         " batched BOOLEAN not null,"+
                         " indexed BOOLEAN not null,"+
                         "PRIMARY KEY (id))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
        }catch(SQLException e){e.printStackTrace();}

        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE WORDS " + 
                         "(id INTEGER not NULL AUTO_INCREMENT, " + 
                         " Name VARCHAR(255), "+ 
                         " IDF FLOAT,"+
                         "PRIMARY KEY (id))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
        }catch(SQLException e){e.printStackTrace();}
        // try{
        //     Statement stmt = conn.createStatement();
        //     String sql = "CREATE TABLE History " + 
        //                  "(id INTEGER not NULL AUTO_INCREMENT, " + 
        //                  " Name VARCHAR(255), "+ 
        //                  "PRIMARY KEY (id))";

        //     stmt.executeUpdate(sql);
        //     System.out.println("Created table in given database...");
        // }catch(SQLException e){e.printStackTrace();}

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
                         "FOREIGN KEY (Word_id) references WORDS(id),"+
                         "CONSTRAINT URL_id UNIQUE (Word_id,URL),"+
                         "PRIMARY KEY (id))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
        }catch(SQLException e){e.printStackTrace();}
        
    }

    public void dropTables(){
        try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE crawler";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}
        
        try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE LINKS";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}

        try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE WORDS";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){e.printStackTrace();}
        catch(Exception e){e.printStackTrace();}

        // try{
        //     Statement stmt = conn.createStatement();
        //     String sql = "DROP TABLE History";
        //     stmt.executeUpdate(sql);
        //     System.out.println("Table dropped!!");
        // }catch(SQLException e){e.printStackTrace();}
        // catch(Exception e){e.printStackTrace();}
    }
   
    /******************************************************* Crawler Table  *********************************************************/
    public void addLink_CrawlerTable(String link){
        //System.out.println("Linkkk: "+link);
        //System.out.println("Linkkk2: "+link.length());
        try{
            String sql = "INSERT INTO crawler " +
                         "(link,visited,batched,indexed)"+
                         "VALUES(?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, link);
            pstmt.setBoolean(2, false);
            pstmt.setBoolean(3, false);
            pstmt.setBoolean(4, false);
            pstmt.executeUpdate();
            //System.out.println("Link Added to table");
        }catch(SQLException e){
            //e.printStackTrace();
        }
        
    }
    
    public void markVisitedLink_CrawlerTable(String link){
        try{
            String sql = "UPDATE crawler SET visited = ? WHERE link = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setString(2, link);
            pstmt.executeUpdate();
            //System.out.println("Link update");
        }catch(SQLException e){
            //e.printStackTrace();
        }
    }

    public void resetBatchedLinks_CrawlerTable(){
        try{
            String sql = "UPDATE crawler SET batched = ? WHERE visited = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, false);
            pstmt.setBoolean(2, false);
            pstmt.executeUpdate();
            System.out.println("Reset");
        }catch(SQLException e){
            //e.printStackTrace();
        }
    }

    public void markBatchedLink_CrawlerTable(String link){
        try{
            String sql = "UPDATE crawler SET batched = ? WHERE link = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setString(2, link);
            pstmt.executeUpdate();
            //System.out.println("Link update");
        }catch(SQLException e){
            //e.printStackTrace();
        }
    }

    public boolean checkLink_CrawlerTable(String link){
        boolean canAddToDB = false;
        //System.out.println("link: "+link);
        //System.out.println("link2: "+link.length());
        if(link == null || link =="" || link ==" " || link.length()==0 || link.length() >= 255){
            //System.out.println("linkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
            return false;
        }
        try{
            String sql = "SELECT * FROM crawler WHERE link=?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, link); 
            ResultSet result = pstmt.executeQuery();
            if(result.next() == false){
                //check if downloadable link
                URL url = new URL(link);
                new BufferedReader(new InputStreamReader(url.openStream()));

                canAddToDB = true;
                //System.out.println("Valid");
            }
            else{
                //System.out.println("Invalid");
            }
        }
        catch(SQLException e){
            //e.printStackTrace();
        }
        catch (IOException io) {
            //System.out.println("Error");
        }
        return canAddToDB;
    }

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

    public Queue<Integer> getLinksToVisitIndexer_CrawlerTable(int numberOfReqLinks){
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

    public int getTotalNumberOfLinks_CrawlerTable(){
        try{
            String sql = "SELECT COUNT(id) AS count FROM crawler";   
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

    

    public int getTotalNumberOfDownloadedLinks_CrawlerTable(){

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

    public int getTotalNumberOfIndexedLinks_CrawlerTable(){

        try{
            String sql = "SELECT COUNT(id) AS count FROM crawler WHERE indexed=true";   
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
    
    public int getTotalNumberOfBatchedLinks_CrawlerTable(){

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
    
    public Queue<linkAndID> getLinksToVisitCrawler_CrawlerTable(int numberOfReqLinks){
        Queue<linkAndID> queue = new LinkedList<>();
        try{
            String sql = "SELECT * FROM crawler WHERE visited =? AND batched =? LIMIT ?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, false); 
            pstmt.setBoolean(2, false); 
            pstmt.setInt(3, numberOfReqLinks); 
            ResultSet result = pstmt.executeQuery();
            //int i=0;
            while(result.next()){
                //i++;
                //System.out.println("Number in queue: "+i);
                int id = result.getInt(1);
                String linkToVisit = result.getString(2);
                linkAndID temp = new linkAndID(linkToVisit,id);
                queue.add(temp);
                sql = "UPDATE crawler SET batched = ? WHERE id = ?";   
                pstmt = conn.prepareStatement(sql);
                pstmt.setBoolean(1, true); 
                pstmt.setInt(2, id); 
                pstmt.executeUpdate();
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
            String sql = "SELECT id FROM words where Name = ?";   
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
            String sql = "INSERT INTO words " +
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
            String sql = "UPDATE words SET" +
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
