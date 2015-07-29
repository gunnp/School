import java.util.*;
import java.io.*;
import java.lang.*;

public class Wordifier {
	/////////////////////////////////////////////////////////////
	// initial variables
   static int totalBigrams = 0;													// a running total of all bigrams in the hash map
	// static LinkedList<String> bigrams = new LinkedList<String>();					// creates a linkedlist to track all unique bigrams
	
	/////////////////////////////////////////////////////////////
	
	
    // loadSentences
    // Preconditions:
    //    - textFilename is the name of a plaintext input file
    // Postconditions:
    //  - A LinkedList<String> object is returned that contains
    //    all of the tokens in the input file, in order
    // Notes:
    //  - If opening any file throws a FileNotFoundException, print to standard error:
    //        "Error: Unable to open file " + textFilename
    //        (where textFilename contains the name of the problem file)
    //      and then exit with value 1 (i.e. System.exit(1))
   public static LinkedList<String> loadSentences( String textFilename ) {
   	/*Date Modified: 07/13/2015 by Sam and Kenny
   	 * Subversion not fully completed, also Paul was not here for implemetation on program.
   	 *take in input from user and read the file into a linked list .
   	 */
      // create linked list
      LinkedList<String> strings = new LinkedList<String>();
      
      try {
      // declare file
         File inputFile = new File(textFilename);
         
          // read the data from the file
         Scanner scannerFile = new Scanner(inputFile);
          
          // get next value from scanner
         while (scannerFile.hasNext()){
          	// add scanner word to linked list
            strings.add(scannerFile.next());
         }
      } 
      catch (FileNotFoundException ex) {
         // 
         System.out.print(ex);
      }
      
       // spit it out
      return strings;
   }	
	
	
	
	
	
    // findNewWords
    // Preconditions:
    //    - bigramCounts maps bigrams to the number of times the bigram appears in the data
    //    - scores maps bigrams to its bigram product score 
    //    - countThreshold is a threshold on the counts
    //    - probabilityThreshold is a threshold on the bigram product score 
    // Postconditions:
    //    - A HashSet is created and returned, containing all bigrams that meet the following criteria
    //        1) the bigram is a key in bigramCounts
    //        2) the count of the bigram is >= countThreshold
    //        3) the score of the bigram is >= probabilityThreshold
    //      Formatting note: the returned HashSet should include a space between bigrams
   public static HashSet<String> findNewWords( HashMap<String,Integer> bigramCounts, HashMap<String,Double> scores, int countThreshold, double probabilityThreshold ) {
   	// create a new hashset
      HashSet<String> verifiedBigrams = new HashSet<String>();
       
       // create key set
      Iterator<String> itr = bigramCounts.keySet().iterator();
       
       // test each bigram
      while (itr.hasNext()) {
       	// looping bigrams
         String key = (String) itr.next();
         
         if ((bigramCounts.get(key) >= countThreshold) && (scores.get(key) >= probabilityThreshold)) {
         	// passed the tests; add bigram to hashset
            verifiedBigrams.add(key);
         }
      }
       	    
       // spit out the damn thing
      return verifiedBigrams;
   }

	
	
	
	
	
    // resegment
    // Preconditions:
    //    - previousData is the LinkedList representation of the data
    //    - newWords is the HashSet containing the new words (after merging)
    // Postconditions:
    //    - A new LinkedList is returned, which contains the same information as
    //      previousData, but any pairs of words in the newWords set have been merged
    //      to a single entry (merge from left to right)
    //
    //      For example, if the previous linked list contained the following items:
    //         A B C D E F G H I
    //      and the newWords contained the entries "B C" and "G H", then the returned list would have 
    //         A BC D E F GH I
   public static LinkedList<String> resegment( LinkedList<String> previousData, HashSet<String> newWords ) {
   	// create a new linked list for merged bigrams
      LinkedList<String> mergedBigrams = new LinkedList<String>();
   	
   	// create iterator
      Iterator<String> dataItr = previousData.iterator();
   	
      String x1 = null;
      String x2 = null;
   	
      while (dataItr.hasNext()) {
      	// get bigrams
         if (x1 == null) {
            x1 = dataItr.next();
         }
      	
         if (dataItr.hasNext()) {
            x2 = dataItr.next();
         	
            String tempBigram = x1 + x2;
            String properBigram = x1 + " " + x2;
         	
         	// test
            if (newWords.contains(properBigram)) {
            	// is a bigram, add bigram to new LinkedList
               mergedBigrams.add(tempBigram);
               x1 = null;
               x2 = null;
            } 
            else {
            	// not a bigram; add x1
               mergedBigrams.add(x1);
               x1 = x2;
               x2 = null;
            }
         }
      }
   	
   	// spit it out
      return mergedBigrams;
       
   }
	
	
	
	
	
	
    // computeCounts
    // Preconditions:
    //    - data is the LinkedList representation of the data
    //    - bigramCounts is an empty HashMap that has already been created
    // Postconditions:
    //    - bigramCounts maps each bigram appearing in the data to the number of times it appears
   public static void computeCounts(LinkedList<String> data, HashMap<String,Integer> bigramCounts ) {
   	// iterate
      Iterator<String> thisItr = data.iterator();
   	
      String x1 = null;
      String x2 = null;
   	
      while (thisItr.hasNext()) {
            // create a bigram
         if (x1 == null) {
            x1 = thisItr.next();
         }
      	
      	// test if x2 exists
         if (thisItr.hasNext()) {
         	// has 2 values
            x2 = thisItr.next();
            String properBigram = x1 + " " + x2;
         	
         	// System.out.println(properBigram);
         	
              // increment
            incrementHashMap(bigramCounts,properBigram,1);
              		        
            x1 = x2;
            x2 = null;
         }
      }
   }

	
	
	
	
	
	
	
    // convertCountsToProbabilities 
    // Preconditions:
    //    - bigramCounts maps each bigram appearing in the data to the number of times it appears
    //    - bigramProbs is an empty HashMap that has already been created
    //    - leftUnigramProbs is an empty HashMap that has already been created
    //    - rightUnigramProbs is an empty HashMap that has already been created
    // Postconditions:
    //    - bigramProbs maps bigrams to their joint probability
    //        (where the joint probability of a bigram is the # times it appears over the total # bigrams)
    //    - leftUnigramProbs maps words in the first position to their "marginal probability"
    //    - rightUnigramProbs maps words in the second position to their "marginal probability"
   public static void convertCountsToProbabilities(HashMap<String,Integer> bigramCounts, HashMap<String,Double> bigramProbs, HashMap<String,Double> leftUnigramProbs, HashMap<String,Double> rightUnigramProbs ) {
   	// loop the bigramCounts hashmap
      
      // get total bigrams count
      int newTotalBigrams = 0;
      double thisProb = 0.0;
      for (int num : bigramCounts.values()) {
         // 
         newTotalBigrams += (int) num;
      }
      
      
      // make temporary tables for left and right values
      HashMap<String,Integer> leftUni = new HashMap<String,Integer>();
      HashMap<String,Integer> rightUni = new HashMap<String,Integer>();
      
            
      // probs
      // create key set
      Iterator<String> probItr = bigramCounts.keySet().iterator();
       
       // test each bigram
      while (probItr.hasNext()) {
       	// looping bigrams
         String key = (String) probItr.next();
         
         // calculate joint prob
         thisProb = (double) bigramCounts.get(key) / newTotalBigrams;
         bigramProbs.put(key, thisProb);
       	
       	
       	// populate left and right unigram counts
         String[] uniSplit = key.split(" ");
         String thisLeftUni = uniSplit[0];
         String thisRightUni = uniSplit[1];
       	
       	
       	// test if left unigram exists; if it does increment; if not add
         if (leftUni.containsKey(thisLeftUni)) {
         	// does contain; increment
            leftUni.put(thisLeftUni, leftUni.get(thisLeftUni) + 1);
         } 
         else {
         	// does not contain; add new
            leftUni.put(thisLeftUni, 1);
         }
       	
       	// test if right unigram exists; if it does increment; if not add
         if (rightUni.containsKey(thisRightUni)) {
         	// does contain; increment
            rightUni.put(thisRightUni, rightUni.get(thisRightUni) + 1);
         } 
         else {
         	// does not contain; add new
            rightUni.put(thisRightUni, 1);
         }
      }
   
      
      // iterate each left uni key
      Set<String> leftUniKeys = leftUni.keySet();
      Iterator<String> leftUniKeysItr = leftUniKeys.iterator();
      
      // loops all left unis
      while (leftUniKeysItr.hasNext()) {
         // this key
         String thisKey = (String) leftUniKeysItr.next();
         
         // calculate left unigram prob
         double thisValue = (double) leftUni.get(thisKey) / newTotalBigrams;
         
         // insert into leftUnigramProbs
         leftUnigramProbs.put(thisKey, thisValue);
      }
      
      
      // iterate each right uni key
      Set<String> rightUniKeys = rightUni.keySet();
      Iterator<String> rightUniKeysItr = rightUniKeys.iterator();
      
      // loops all right unis
      while (rightUniKeysItr.hasNext()) {
         // this key
         String thisKey = (String) rightUniKeysItr.next();
         
         // calculate right unigram prob
         double thisValue = (double) rightUni.get(thisKey) / newTotalBigrams;
         
         // insert into rightUnigramProbs
         rightUnigramProbs.put(thisKey, thisValue);
      }
   }

	
	
	
	
	
   
   
	
    // getScores
    // Preconditions:
    //    - bigramProbs maps bigrams to to their joint probability
    //    - leftUnigramProbs maps words in the first position to their probability
    //    - rightUnigramProbs maps words in the first position to their probability
    // Postconditions:
    //    - A new HashMap is created and returned that maps bigrams to
    //      their "bigram product scores", defined to be P(w1|w2)P(w2|w1)
    //      The above product is equal to P(w1,w2)/sqrt(P_L(w1)*P_R(w2)), which 
    //      is the form you will want to use
   public static HashMap<String,Double> getScores( HashMap<String,Double> bigramProbs, HashMap<String,Double> leftUnigramProbs, HashMap<String,Double> rightUnigramProbs ) {
   	// loop bigrams
      HashMap<String,Double> bigramScores = new HashMap<String,Double>();		// the HashMap to store the bigram scores
      
      // probs
      // create key set
      Set keySet = bigramProbs.keySet();
      Iterator probItr = keySet.iterator();
       
      
     // test each bigram
      while (probItr.hasNext()) {
        // looping bigrams
         String thisBigram = (String) probItr.next();
        
        // split the bigram
         String[] bigramSplit = thisBigram.split(" ");
         String leftUnigram = bigramSplit[0];
         String rightUnigram = bigramSplit[1];
        
        
        // calc score
         double tempBigramProbs = (double) bigramProbs.get(thisBigram);
         double tempLeftUnigramProbs = (double) leftUnigramProbs.get(leftUnigram);
         double tempRightUnigramProbs = (double) rightUnigramProbs.get(rightUnigram);
      
         double score = (double) (tempBigramProbs / Math.sqrt(tempLeftUnigramProbs * tempRightUnigramProbs));
      
        // add to new HashMap
         bigramScores.put(thisBigram, score);
      }
      
      // spit out the damn thing
      return bigramScores;
   }

	
	
	
	
	
	
    // getVocabulary
    // Preconditions:
    //    - data is a LinkedList representation of the data
    // Postconditions:
    //    - A new HashMap is created and returned that maps words
    //      to the number of times they appear in the data
   public static HashMap<String,Integer> getVocabulary( LinkedList<String> data ) {
   	//Date Modified: 07/13/2015 by Sam and Kenny
   	//Referenced "Building Java Programs" 3rd Edition
      HashMap<String,Integer> wordCounts = new HashMap<String,Integer>();		// the HashMap to store the bigram scores
      
      // loop each linkedlist entry
      for (int s=0; s < data.size(); s++){
         // gets the next word
         String word = data.get(s).toLowerCase();
         
         // increment
         incrementHashMap(wordCounts,word,1);
      }
      
      // spit it out
      return wordCounts;
   }

	
	
	
	
	
	
   // loadDictionary
   // Preconditions:
   //    - dictionaryFilename is the name of a dictionary file
   // Postconditions:
   //    - A new HashSet is created and returned that contains
   //      all unique words appearing in the dictionary
   public static HashSet<String> loadDictionary( String dictionaryFilename ) {
      // Date Modified: 07/13/2015 by Sam and Kenny
      // create new hashset
      HashSet<String> words = new HashSet<String>();
      
      // create linked list
      LinkedList<String> dictStrings = new LinkedList<String>();
      
      try {
         // open the file
         File dictFile = new File(dictionaryFilename);
      	
      	// test to see if file can be read
         if (!dictFile.canRead()) {
         	// file can't be read; throw exception
            System.out.println("The file " + dictionaryFilename + " cannot be opened for input.");
            System.exit(0);
         }
      	
         // read the data from the file
         Scanner dictScanner = new Scanner(dictFile);
          
         // get next value from scanner
         while (dictScanner.hasNext()){
            // add scanner word to linked list
            dictStrings.add(dictScanner.next());
         }      
         
         
         
         // copy unique words from dictionary into words
         for (int i=0; i<dictStrings.size(); i++) {
            // test to see if it exists
            if (!words.contains(dictStrings.get(i))) {
               // doesn't exist; add unique value to dictionary
               words.add(dictStrings.get(i));
            }
         }
      } 
      catch (FileNotFoundException ex) {
         System.out.print(ex);
      }
      
      
      // spits out the damn dictionary
      return words;
   }

	
	
	
	
	
	
	
	
    // incrementHashMap
    // Preconditions:
    //  - map is a non-null HashMap 
    //  - key is a key that may or may not be in map
    //  - amount is the amount that you would like to increment key's value by
    // Postconditions:
    //  - If key was already in map, map.get(key) returns amount more than it did before
    //  - If key was not in map, map.get(key) returns amount
    // Notes:
    //  - This method has been provided for you 
   private static void incrementHashMap(HashMap<String,Integer> map,String key,int amount) {
      if( map.containsKey(key) ) {
         map.put(key,map.get(key)+amount);
      } 
      else {
         map.put(key,amount);
      }
      return;
   }

	

	
    // printNumWordsDiscovered
    // Preconditions:
    //    - vocab maps words to the number of times they appear in the data
    //    - dictionary contains the words in the dictionary
    // Postconditions:
    //    - Prints each word in vocab that is also in dictionary, in sorted order (alphabetical, ascending)
    //        Also prints the counts for how many times each such word occurs
    //    - Prints the number of unique words in vocab that are also in dictionary 
    //    - Prints the total of words in vocab (weighted by their count) that are also in dictionary 
	// Notes:
    //    - See example output for formatting
   public static void printNumWordsDiscovered( HashMap<String,Integer> vocab, HashSet<String> dictionary ) {
       // comparing suspected bigram words to the dictionary of actual words
   	
   	// create new keyset
      LinkedList<String> keys = new LinkedList<String>();
      
      double totalTokens = 0;
      double discoveredTokens = 0;
      
      for (String key : vocab.keySet()) {
         keys.add(key);
         totalTokens += (int) vocab.get(key);
      }
      
      // create new hashmap
      HashMap<String,Integer> preSortHash = new HashMap<String,Integer>();
   	
   	// loop vocab
      for (int i=0; i < vocab.size(); i++) {
      	// convert to lowercase and compare against dictionary
         // get value from hashmap
         String tempValue = keys.get(i);
         
         if (dictionary.contains(tempValue)) {
         	// is a word; add to new hash map
            preSortHash.put(tempValue,vocab.get(tempValue));
            discoveredTokens += (int) vocab.get(tempValue);
         }
      }
   	
   	// sort the hash map
   	// create LinkedList
      List<String> listSort = new ArrayList<String>();
   	
      
      for (String key : preSortHash.keySet()) {
         // current word (key)
         String thisWord = key;
         
         // add if exists; otherwise do nothing
         if (!listSort.equals(thisWord)) {
         	// does not exist; add new
            listSort.add(thisWord);
         }
      }
      
   	// sort
      Collections.sort(listSort);
   	
   	// print the damn thing
   	// System.out.print(listSort);
   	
      double totalWords = 0;
   	
      for (int i = 0; i<listSort.size(); i++) {
         String thisKey = listSort.get(i);
         int thisValue = vocab.get(listSort.get(i));
         totalWords++;
         System.out.println("Discovered " + thisKey + " (count " + thisValue + ")");
      }
   	
      int dic = dictionary.size();
      double ratio = (double) (totalWords / dic);
   	
      System.out.println();
      System.out.print("Discovered " + (int) totalWords + " actual (unique) words out of " + dictionary.size() + " dictionary words (");
      System.out.format("%.2f", ratio*100);
      System.out.println("%)");
   	
   	
      double tokenRatio = (double) (discoveredTokens / totalTokens);
      System.out.print("Discovered " + (int) discoveredTokens + " actual word tokens out of " + (int) totalTokens + " total tokens (");
      System.out.format("%.2f", tokenRatio*100);
      System.out.println("%)");
   }

}
