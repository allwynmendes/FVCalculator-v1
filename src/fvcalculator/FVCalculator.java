/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fvcalculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;


public class FVCalculator {
   
    static double step = 0.5;
    static double range = 10;
    static DBConnect db1 = new DBConnect();
    static double maxDiscount;
    static int maxCCount;
    
    public static void readCsv(String fileName) throws IOException, SQLException, ClassNotFoundException{
        String splitBy = ",";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = br.readLine();
        System.out.println("Discounts  ");
        while((line = br.readLine()) != null){
            String[] b = line.split(splitBy);
            System.out.println(b[0]);
            db1.insertStocksDB(Double.parseDouble(b[0]));
        }
        br.close();
    }
    
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        //Values in DB
        /*readCsv("discounts.csv");*/
        maxDiscount = db1.calcMaxDiscount();
        System.out.println("MAX : " + maxDiscount);
        db1.truncateTable("BucketTable");
        db1.calcBuckets(step, range, maxDiscount);
        maxCCount = db1.calcMaxCCount();
        System.out.println("\n\n" + maxCCount);
        db1.calcLowHighMid(maxCCount, range);
    }
    
}
