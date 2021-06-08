package DBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;


public class DBManager {
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
    
    public DBManager(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        catch(SQLException e){}
        catch(Exception e){}
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
        }catch(SQLException e){}

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
        }catch(SQLException e){}
        
         try{
             Statement stmt = conn.createStatement();
             String sql = "CREATE TABLE History " + 
                          "(id INTEGER not NULL AUTO_INCREMENT, " + 
                          " Name VARCHAR(255), "+ 
                          "PRIMARY KEY (id))";

             stmt.executeUpdate(sql);
             System.out.println("Created table in given database...");
        }catch(SQLException e){}

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
        }catch(SQLException e){}
        
    }

    public void dropTables(){
        try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE crawler";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){}
        catch(Exception e){}
        
        try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE LINKS";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){}
        catch(Exception e){}

        try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE WORDS";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){}
        catch(Exception e){}

        //uncomment to drop history table
        /*try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE History";
            stmt.executeUpdate(sql);
            System.out.println("Table dropped!!");
        }catch(SQLException e){}
        catch(Exception e){}*/
    }
   
    /******************************************************* Crawler Table  *********************************************************/
    public void addLink_CrawlerTable(String link){
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
        }catch(SQLException e){ }  
    }
    
    public void markVisitedLink_CrawlerTable(String link){
        try{
            String sql = "UPDATE crawler SET visited = ? WHERE link = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setString(2, link);
            pstmt.executeUpdate();
        }catch(SQLException e){ }
    }

    public void resetBatchedLinks_CrawlerTable(){
        try{
            String sql = "UPDATE crawler SET batched = ? WHERE visited = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, false);
            pstmt.setBoolean(2, false);
            pstmt.executeUpdate();
            System.out.println("Reset");
        }catch(SQLException e){ }
    }

    public void markBatchedLink_CrawlerTable(String link){
        try{
            String sql = "UPDATE crawler SET batched = ? WHERE link = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setString(2, link);
            pstmt.executeUpdate();
        }catch(SQLException e){ }
    }

    public boolean checkLink_CrawlerTable(String link){
        boolean canAddToDB = false;
        if(link == null || link =="" || link ==" " || link.length()==0 || link.length() >= 255){
            return false;
        }
        
        try{
            String sql = "SELECT * FROM crawler WHERE link=?";   
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, link); 
            ResultSet result = pstmt.executeQuery();
            if(result.next() == false){
                canAddToDB = true;
            }
        }
        catch(Exception e){ }

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
        catch(SQLException e){ }
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
        catch(SQLException e){ }
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
            while(result.next()){
                int id = result.getInt(1);
                queue.add(id);
            }
        }
        catch(SQLException e){ }
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
        catch(SQLException e){ }
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
        catch(SQLException e){ }
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
        catch(SQLException e){ }
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
        catch(SQLException e){ }
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
            while(result.next()){
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
        catch(SQLException e){ }
        return queue;
    }
    
    public void markIndexedLink_CrawlerTable(int id){
        try{
            String sql = "UPDATE crawler SET indexed = ? WHERE id = ?";        
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, true);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }catch(SQLException e){  }
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
        catch(SQLException e){ }
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
        catch(SQLException e){ }
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
        catch(SQLException e){ }
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
        }catch(SQLException e){  }
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

        }catch(SQLException e){  }
    }

    public void addLink_WordsTable(String URL,int TF,int Plain,int Header,int Title,String Name){
		int wordID= getWordID_WordsTable(Name);
		if(wordID == -1){
            addWord_WordsTable(Name);
			wordID = getWordID_WordsTable(Name);
        }
            
        try{
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
        }catch(SQLException e){ }

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
