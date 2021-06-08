# Web-Search-Engine

### Note
Before any step please clean and reinstall the project using maven.

## For crawling and indexing
1- Go to ./src/main/java/DBManager
2- Open DBManager.java and change database username and password
3- Go to ./src/main/java/Main
4- open Main.java and set the settings at the begining of the file (i.e. number of threads and crawling size)
5- Run Main.java

Note: you can limit the number of links you want to add to the database using DBMaxSize variable

## For Running the backend
1- Go to ./src/main/resources
2- Open application.properties and change username and password
3- Go to ./src/main/java/Backend/jpa
4- Open MainApplication.java
5- Run java

## For Running the frontend
1- Go to ./UI/google
2- Run npm install
3- Run npm start
