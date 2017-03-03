import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.ArrayList;


/* This code is available at:
*	https://github.com/pikawolfy/CSCI3104-Spring17
*/

/*	HOW TO RUN ME:
*
*	The histogram is displayed in the console, so the program
*	should be compiled and executed as such.
*
*	javac Problem1.java
*	java PS4 [input file]
*/

/*
*   Script for Problem Set 5, problem 1.
*
*	Takes a string as an argument to be encoded.
*
*	Implements the pseudocode provided in the write up to encode the string.
*
*	Uses a handwritten priority queue structure to implement huffmanEncode.
*/

public class Problem1 {

	/* Variables for managing the queue of characters*/
	static int lastIndex;
	static node queue[] = new node[8193];

	static int huffmanAtomics = 0;

	/* Node struct holding character id, frequency, and children (when encoding) */
	public static class node {
	    public int frequency;
        public String id;
        public node right;
        public node left;
	 };

	 /* Contains S and f */
	 public static class symbolSet {
	    public char[] S;
	    public int[] f;
	 };

	 /* Contains the characters and their final encoding */
	public static class encode {
	    public char[] symbol;
	    public String[] code;
	 };


	 public static void main(String [] args) {
    	if (args.length > 0) {

    		try{
     			String content = new Scanner(new File(args[0])).useDelimiter("\\Z").next();

     			/* Use the three lines below to run the whole program */

     			// String y = encodeString(content, huffmanEncode(string2freq(content)));
     			// System.out.println(y);
     			// System.out.println("Length of y is " + y.length());

     			/* Lines below used to find the minimal bit size */

	     		symbolSet mySet = string2freq(content);
	     		double sum = 0;

	     		for (int i = 0; i < mySet.S.length; i++) {
	     			//System.out.println(mySet.S[i] + " has a freq of " + mySet.f[i]);
	     			//System.out.println(((double)mySet.f[i] / (double)content.length()) + " * " + ((double)Math.log10((double)mySet.f[i] / (double)content.length()) / (double)Math.log10(2)));
	     			sum += ((double)mySet.f[i] / (double)content.length()) * ((double)Math.log10((double)mySet.f[i] / (double)content.length()) / (double)Math.log10(2));
	     		}
	     		sum = sum * -1;
	     		System.out.println("The number of bits required is " + sum);
     		}
     		catch (IOException e) {
		      e.printStackTrace();
			}
        }
    }


     /* Create S and f.
     *	
     *	Creates S, sorted lexicographic order where each symbol only appears once.
     *
     *	Creates f based on S and the frequency of each symbol in the original string x.
     *
     *	Returns the set containing these two.
     */
    static symbolSet string2freq(String x) { 
    	symbolSet mySet = new symbolSet();
    	Set<Character> uniq = new HashSet<Character>(); // A HashSet is used to remove duplicates
		for(char c : x.toCharArray()) {
		    uniq.add(c);
		}
		mySet.S = new char[uniq.size()];
		int k = 0;
		for(char c : uniq) {
			mySet.S[k] = c;
			k++;
		}
		Arrays.sort(mySet.S); // Sort to lexicographic order
		mySet.f = new int[mySet.S.length*2];
		for(char c : x.toCharArray()) { // Count frequencies
			mySet.f[new String(mySet.S).indexOf(c)]++; 
		}

    	return mySet;
    }


    /* 	Create the priority queue and encoding of the characters.
     *	
     *	Builds a priority queue to used for encoding, based on pseudocode provided.
     *
     *	Creates T, the "dictionary" of encodings, by calling createT.
     *
     *	Returns T.
     */
    static encode huffmanEncode(symbolSet mySet) { 
    	lastIndex = 0;
    	int n = mySet.S.length;
    	for (int i = 0; i < n; i++) { 
    		insert(String.valueOf(mySet.S[i]),mySet.f[i], null, null);
    	} 
    	for (int k = n+1; k < (2*n); k++) { 
    		node i = deletemin();
    		node j = deletemin();
    		String s = i.id + j.id;
    		mySet.f[k] = i.frequency + j.frequency; 
    		insert(s,mySet.f[k],i,j);
    	} 
    	encode T = new encode();
    	T.symbol = new char[mySet.S.length];
    	T.code = new String[mySet.S.length];
    	for (int i = 0; i < mySet.S.length; i++) {
    		T.symbol[i] = mySet.S[i];
    	}

    	Arrays.fill(T.code," ");
    	T = createT(queue[1],T,null);
    	// for (int i = 0; i < T.symbol.length; i++) {
    	// 	System.out.println(T.symbol[i] + "'s code is " + T.code[i].replaceAll(" ",""));
    	// }
    	return T;
    }


    /* 	Takes a string x and the dictionary T to encode the string binarily.
     *	
     *	Returns the encoded string.
     */
    static String encodeString(String x, encode T) { 
    	String y = " ";
    	for (char c : x.toCharArray()) { 
    		y += T.code[new String(T.symbol).indexOf(c)]; // Append each symbol's encoding
    	} 
    	return y.replaceAll(" ","");
    }


    /* 	Traverses the tree to assign 0s and 1s for each character.
     *	
     *	Returns the encoded dictionary T. Initially, dir is null.
     */
    static encode createT(node n, encode T, String dir) {
		if(dir == "right") {
			for (char c : n.id.toCharArray()) {
				T.code[new String(T.symbol).indexOf(c)] += "0"; // Give each symbol in the right child id a 0
			}
		}
		else if(dir == "left") {
			for (char c : n.id.toCharArray()) {
				T.code[new String(T.symbol).indexOf(c)] += "1"; // Give each symbol in the left child id a 1
			}
		}
		// Traverse 
		if(n.left != null) {
			createT(n.left, T, "left");
		}
		if(n.right != null) {
			createT(n.right, T, "right");
		}
		return T;
	}


	static symbolSet makeHuffmanInput(int n) { // n is the number of symbols 
		symbolSet newSet = new symbolSet();
		newSet.f = new int[n*2];
		newSet.S = new char[n];
		for(int i = 0; i < n; i++) {
			newSet.f[i] = ((int)((Math.random()*100)+1)); // random frequency from 1..100 
			newSet.S[i] = ('c');
		}
		return newSet;
	}



	/* PRIORITY QUEUE DATA STRUCTURE */
    

	/* 	Checks if the queue is empty.
	*
	*	Somwhat useless for our purposes.
	*/
    static boolean isEmpty() {
		if (lastIndex == 0) {
			return true;
		}
		else {
			return false;
		}
	}


	/* 	Inserts a node with log(n) time (tree traversal);
	*
	*	Assigns properties, calculates position in tree, and then shifts accordingly.
	*/
	static void insert(String id, int freq, node child1, node child2) {
		lastIndex++;
		node newNode = new node();
		newNode.frequency = freq;
		newNode.id = id;
		
		if (child1 != null) { // Determines right and left children for encoding traversal later
			if (child1.frequency < child2.frequency) {
				newNode.left = child1;
				newNode.right = child2;
			}
			else if(child1.frequency > child2.frequency) {
				newNode.left = child2;
				newNode.right = child1;
			}
			else if(child1.frequency == child2.frequency) { // Random tie handler
				if(Math.random() < 0.5) {
				    newNode.left = child1;
					newNode.right = child2;	
				}
				else {
					newNode.left = child2;
					newNode.right = child1;
				} 
			}
		}
		 
		queue[lastIndex] = newNode;

		int childIndex = lastIndex;
		int parentIndex = childIndex / 2; 

		if (lastIndex > 3) { // Handles shifting of the queue
			boolean recursive = true;
			while (recursive) {
				if (childIndex % 2 == 0 && childIndex > 1) {
					parentIndex = childIndex / 2;
				}
				else if (childIndex > 2) {
					parentIndex = (childIndex - 1) / 2;
				}

				if (queue[childIndex].frequency < queue[parentIndex].frequency) {
					recursive = true;
					swap(childIndex, parentIndex);
					childIndex = parentIndex;
				}
				else {
					recursive = false;
				} 
			}
		}
		 
	}


	/* 	Swaps two nodes in the queue */
	static void swap(int index1, int index2) {
		node temp = queue[index1];
		queue[index1] = queue[index2];
		queue[index2] = temp;
	}


	/* 	Deletes minimal value with log(n) tme (tree traversal).
	*
	*	After deleting, sorts the queue accordingly.
	*
	*	Returns deleted node.
	*/
	static node deletemin() {
		node next = queue[1];
		if (lastIndex != 1) {
			queue[1] = queue[lastIndex];

			node root = queue[1];
			node left = queue[2];
			node right = queue[3];
			int rootIndex = 1;
			int leftIndex = 2; 
			int rightIndex = 3;

			while (rootIndex < lastIndex / 2) {

				boolean leftMax = false;
				boolean rightMax = false;

				for (int i = 1; i < lastIndex+1; i++) {
					if (root.id == queue[i].id) {
						root = queue[i];
						rootIndex = i;
						break;
					}
				}
				
				if (rootIndex < lastIndex / 2) {

					leftIndex = 2*rootIndex;
					left = queue[leftIndex];
					rightIndex = (2*rootIndex) + 1;
					right = queue[rightIndex];
					 
					if (root.frequency > left.frequency) {
						leftMax = true;
					}
					 
					if (root.frequency > right.frequency) {
						rightMax = true;
					}
					 

					if (leftMax && rightMax) {
						if (left.frequency < right.frequency) {
							swap(leftIndex, rootIndex);
						}
						else {
							swap(rightIndex, rootIndex);
						}
					}
					else if (leftMax && !rightMax) {
						swap(leftIndex, rootIndex);
					}
					else if (!leftMax && rightMax) {
						swap(rightIndex, rootIndex);
					}
					else {
						break;
					}	 
				}
			}

			if(lastIndex == 3) {
				if (root.frequency > left.frequency && root.frequency <= right.frequency) {
						swap(leftIndex, rootIndex);
				}
				else if (root.frequency > right.frequency && root.frequency <= left.frequency) {
						swap(leftIndex, rootIndex);
				} 
			}
		}
		 
		lastIndex--;
		 
		if(isEmpty()) {
			queue[0] = null;	 
		}
		 
		return next;
	}
}