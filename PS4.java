import java.io.*;
import java.io.File;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Random;


/* This code is available at:
*	https://github.com/pikawolfy/CSCI3104-Spring17
*/

/*	HOW TO RUN ME:
*
*	The histogram is displayed in the console, so the program
*	should be compiled and executed as such.
*
*	javac PS4.java
*	java PS4 [filename.txt]
*/

/*
*   Script for Problem Set 4, problem 4.
*
*	Takes the provided Census data as the only argument, as a .txt file.
*
*	Randomly generates a selection of names 50% the size
*	of the original data.
*
*	Calculates hash values for each name and records table entries
*	using the bucket array.
*
*	Prints a histogram to the console representing the distribution
*	of table entries in the hash table.
*/
public class PS4 {

	static double[] bucket = new double[200];
	static ArrayList<String> allNames = new ArrayList<String>();
	static ArrayList<String> selectedNames = new ArrayList<String>();

	public static void main(String [] args) {
    	if (args.length > 0) {

        	File lastNames = new File(args[0]);

        	selectNames(lastNames);
        	hash();
        	histogram();
        }
    }

    /*
    *	Randomly selects an index value from the size of available names.
    *	Pulls the random name and appends it to the selectedNames array list.
    *	Removes the name from the total list so it may not be selected again.
    */
    static void selectNames(File lastNames) {
    	try {
	    	BufferedReader reader = new BufferedReader(new FileReader(lastNames));
			int numNames = 0;
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				numNames++;
				String[]split = line.split(" ");
				allNames.add(split[0]);
			}
			reader.close();

			Random rand = new Random();

			while (selectedNames.size() != numNames/2) {
				int randNum = rand.nextInt(allNames.size());
				
				String name = allNames.get(randNum);
				selectedNames.add(name);
				allNames.remove(name);
				//System.out.println(name);

			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    }

    /*
    *	Generates a character's value based on ASCII values.
    */
    static int getCharVal(char letter) {
    	return (int) letter -64;
    }

    /*
    *	For all of the selected names, adds a count to the bucket
    *	index corresponding with the hash value.
    */
    static void hash() {
    	for (String name : selectedNames) {
    		int hashVal = calculateHashValue(name);
    		bucket[hashVal] ++;
    	}
    	
    }

    /*
    *	Calculates the hash value for a name by summing the
    *	character values in the name and modulo(ing) by the
    *	bucket size, 200.
    */
    static int calculateHashValue(String name) {
    	int sum = 0;
    	char [] chars = name.toCharArray();
    	for (char c : chars) {
    		sum += getCharVal(c);
    	}
    	return (sum % 200);
    }

    /*
    *	Draws the histogram of table entry distribution to the console, 
    *	scaled by 12, running vertically from highest hash values to smallest.
    *	Loosely based from http://courses.cs.washington.edu/courses/cse142/10sp/lectures/5-19/~programs/Histogram.java
    *	Draws basic information for labeling to axes.
    */
    static void histogram() {
        
        double max = 0;
       	String y = "HASH VALUE     ";
        char [] yaxis = y.toCharArray();
        int yaxisIndex = 0;
        // print star histogram
        for (int i = bucket.length-1; i > 0; i--) {
            if (bucket[i] > 0) {
            	System.out.print(" " + yaxis[yaxisIndex]);
            	yaxisIndex++;
            	if(yaxisIndex == 15) {yaxisIndex = 0;}
                double scaledVal = bucket[i] / 12;
               	if (bucket[i] > max) { max = bucket[i];}
                if (scaledVal > 0) {
                	System.out.print("        " + i + ": ");
       				if (i < 10) { System.out.print(" ");}
       				if (i < 100) { System.out.print(" ");}
	                for (double j = 0; j < scaledVal; j++) {
	                    System.out.print("*");
	                }
	                System.out.println();
	            }
            }
        }
        System.out.println();
        System.out.print("               0");
        System.out.format("%"+max/12/2+"s", "");  
        System.out.print((int) max/2);
        System.out.format("%"+max/12/2+"s", "");  
        System.out.println((int) max);
        System.out.println();
        System.out.print("                ");
        System.out.format("%"+max/12/3+"s", ""); 
        System.out.println("NUMBER OF TABLE ENTRIES");
    }
}