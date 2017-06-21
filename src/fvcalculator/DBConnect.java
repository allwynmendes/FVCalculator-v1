/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fvcalculator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author inrp10181
 */
public class DBConnect {
    void insertStocksDB(Double discounts) throws SQLException, ClassNotFoundException{
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/FVCalculator;create=true", "revgurus", "revgurus"); // The database URL may not be same for you, lookup the "Services" side-bar.
        Statement stmt = con.createStatement();
        stmt.execute("insert into discounts_1 values(" +discounts+")");
        con.close();
    }
    
    Double calcMaxDiscount() throws ClassNotFoundException, SQLException{
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/FVCalculator;create=true", "revgurus", "revgurus"); // The database URL may not be same for you, lookup the "Services" side-bar.
        Statement stmt = con.createStatement();      
        ResultSet rs = stmt.executeQuery("SELECT max(discounts) FROM discounts_1");
        
        rs.next();
        int max = rs.getInt(1);
        con.close();
        return (double)max;
    }
    
    void calcBuckets(double step, double range, double max) throws ClassNotFoundException, SQLException{
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/FVCalculator;create=true", "revgurus", "revgurus"); // The database URL may not be same for you, lookup the "Services" side-bar.
        Statement stmt = con.createStatement();      
        double i, j, k=0;
        double cCount = 0;//Cumulitive Count
        String query = null;
        for(i=0,j=range; /*i<=range*2 ||*/ k<max; i=i+step, j=j+step){
            if(i<=range){
                //0 - 
                query = "SELECT count(discounts) FROM discounts_1 where discounts >=" + 0 +" and discounts <=" + j;
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                cCount = rs.getInt(1);
                System.out.println(i + "   " + k + "   " + j + "   " + cCount);
            }else{
                //15 - 30
                //15.5 - 30.5
                k = k + step;
                query = "SELECT count(discounts) FROM discounts_1 where discounts >=" + k +" and discounts <=" + j;
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                cCount = rs.getInt(1);
                System.out.println(i + "   " + k + "   " + j + "   " + cCount);
            }
            insertIntoBucketTable(step, k, j, cCount);
        }
    }
    
    void truncateTable(String tableName) throws ClassNotFoundException, SQLException{
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/FVCalculator;create=true", "revgurus", "revgurus"); // The database URL may not be same for you, lookup the "Services" side-bar.
        Statement stmt = con.createStatement();
        stmt.execute("truncate table " + tableName);
        con.close();
    }
    
    void insertIntoBucketTable(double step, double low, double high, double cCount) throws ClassNotFoundException, SQLException{
        int bNumber = 1;
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/FVCalculator;create=true", "revgurus", "revgurus"); // The database URL may not be same for you, lookup the "Services" side-bar.
        Statement stmt = con.createStatement();
        stmt.execute("insert into BucketTable values("+bNumber+", "+step+", "+low+", "+high+","+cCount+")");
        con.close();
    }
    
    int calcMaxCCount() throws ClassNotFoundException, SQLException{
        int maxCCount;
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/FVCalculator;create=true", "revgurus", "revgurus"); // The database URL may not be same for you, lookup the "Services" side-bar.
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select max(ccount) from BucketTable");
        rs.next();
        maxCCount = rs.getInt(1);
        return maxCCount;
    }
    
    void calcLowHighMid(int maxCCount, double range) throws ClassNotFoundException, SQLException{
        double low, high, mid;
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/FVCalculator;create=true", "revgurus", "revgurus"); // The database URL may not be same for you, lookup the "Services" side-bar.
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select min(low), max(high) from BucketTable where CCOUNT = " + maxCCount);
        rs.next();
        low = rs.getDouble(1);
        high = rs.getDouble(2);
        mid = (low + high)/2;
        System.out.println("Low : " + low + " Mid : " + mid + " High : " + high);
        calcDiscount(range, mid);
    }
    
    void calcDiscount(double range, double mid){
        System.out.println("\n\nDiscount %");
        System.out.println("Above MidPoint : " + (mid + range));
        System.out.println("MidPoint       : " + mid);
        System.out.println("Below MidPoint : " + (mid-range));
    }    
}
