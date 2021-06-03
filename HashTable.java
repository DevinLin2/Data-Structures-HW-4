import java.util.*;

public class HashTable {

  private class Entry {

    char item;
    int frequency;
    Entry nextEntry;
    Entry prevEntry;

    private Entry(char element, int freq) {
      item = element;
      frequency = freq;
      nextEntry = null;
      prevEntry = null;
    }

    /**
    Simple constant time function to increase the frequency of the entry
    **/
    private void increaseFrequency() {
      frequency++;
    }

    /**
    Simple constant time function to decrease the frequency of the entry
    **/
    private void decreaseFrequency() {
      frequency--;
    }

    /**
    Simple constant time getter method
    **/
    private char getItem() {
      return item;
    }

    /**
    Simple constant time getter method
    **/
    private int getFrequency() {
      return frequency;
    }

    /**
    Simple constant time setter method
    **/
    private void setNextEntry(Entry entry) {
      nextEntry = entry;
    }

    /**
    Simple constant time setter method
    **/
    private void setPrevEntry(Entry entry) {
      prevEntry = entry;
    }

    /**
    Simple constant time method to check if there is a next entry attached to this
    **/
    private boolean hasNextEntry() {
      if (nextEntry != null) {
        return true;
      }
      return false;
    }

    /**
    Simple constant time method to check if there is a previous entry attached to this
    **/
    private boolean hasPrevEntry() {
      if (prevEntry != null) {
        return true;
      }
      return false;
    }

    /**
    Simple constant time getter method
    **/
    private Entry getNext() {
      return nextEntry;
    }

    /**
    Simple constant time getter method
    **/
    private Entry getPrev() {
      return prevEntry;
    }

  }

  private Entry[] table;
  private int tableSize;
  private float loadFactor;

  public HashTable(int size) {
    table = new Entry[size];
    tableSize = size;
    loadFactor = 0;
  }

  /**
  Constant time hash function to return the index of the element to be inserted at or searched for
  **/
  private int hash(char c) {
    return c % tableSize;
  }

  /**
  Linear O(n) time function to find the next prime number after the given input. THis is necessary to rehash the hash table.
  **/
  private int nextPrime(int num) {
    num++;
    for (int i = 2; i < num; i ++) {
      if (num % i == 0) {
        num++;
        i = 2;
      }
    }
    return num;
  }

  /**
  linear time O(n) function to resize the hash table
  **/
  private void rehash() {
    int oldSize = tableSize;
    Entry[] oldTable = table;
    tableSize = nextPrime(2 * oldSize);
    table = new Entry[tableSize];
    loadFactor = 0;
    for (Entry i : oldTable) { // insert every item from old table into new table
      if (i != null) {
        insert(i.getItem(), i.getFrequency());
        while (i.hasNextEntry()) {
          i = i.getNext();
          insert(i.getItem(), i.getFrequency());
        }
      }
    }
  }

  /**
  Inserts the char c into the hash table using the hash function. If there is a collision, either c already exists or it is simply a collision.
  In the case c already exists in the has table, we increase its frequency. In the case it is simply a collision, we attach the new entry to the end of the list of entries in the bucket.
  Worst case linear time insert function due to rehashing or all the elements being in the same bucket.
  Average constant time O(1) runtime assuming there are no collisions from the elements being spread out or relatively small buckets
  **/
  private void insert(char c, int frequency) {
    if (loadFactor >= 2) {
      rehash();
    }
    int index = hash(c);
    Entry newEntry = new Entry(c, frequency);
    if (table[index] == null) { // there is no collision
      table[index] = newEntry;
    } else { // there is a collision: we use separate chaining to solve this or there is another letter inserted thats already in the hash table
      Entry current = table[index];
      while (current.hasNextEntry() && current.getItem() != c) {
        current = current.getNext();
      }
      if (current.getItem() == c) { // char already exists in table
        current.increaseFrequency();
      } else { // char does not exist in table and the entry is a collision
        current.setNextEntry(newEntry);
        newEntry.setPrevEntry(current);
      }
    }
    loadFactor = (loadFactor * tableSize + 1) / tableSize;
  }

  /**
  Looks for the char c in the hash table and if found removes it from the table.
  Accomplished by hashing c and checking the index of the table for c.
  Either c exists in the bucket or the bucket is empty. If the bucket is not empty it is iterated over to find c.
  Average constant O(1) runtime.
  Worst case O(n) runtime if all elements are in one bucket.
  **/
  private boolean searchAndRemove(char c) {
    int index = hash(c);
    if (table[index] == null) {
      return false;
    }
    Entry current = table[index];
    while (current.hasNextEntry() && current.getItem() != c) { // this will either be the first entry, the last entry, or the entry with c
      current = current.getNext();
    }
    if (current.getItem() != c) {
      return false;
    } else {
      current.decreaseFrequency();
      if (current.getFrequency() == 0) {
        if (!current.hasPrevEntry() && !current.hasNextEntry()) { // only one item in the entry location
          table[index] = null;
        }
        if (!current.hasPrevEntry() && current.hasNextEntry()) { // multiple collided entries and current is the beginning of the list
          table[index] = current.getNext();
          current.getNext().setPrevEntry(null);
        }
        if (current.hasPrevEntry() && !current.hasNextEntry()) { // multiple collided entries and current is the end of the list
          current.getPrev().setNextEntry(null);
        }
        if (current.hasPrevEntry() && current.hasNextEntry()) { // multiple collided entries and current is somewhere in the middle of the list
          Entry next = current.getNext();
          Entry prev = current.getPrev();
          next.setPrevEntry(prev);
          prev.setNextEntry(next);
        }
      }
      return true;
    }
  }

  /**
  Checks if two given strings are anagrams in an average of O(n) time.
  The first string is inserted into a hash table.
  The second string is iterated over and we try to search for every char in the table.
  If a char is not found or the table is not empty at the end, the strings are not anagrams otherwise they are.
  **/
  public boolean checkIfAnagram(String x, String y) {
    table = new Entry[1];
    tableSize = 1;
    loadFactor = 0;
    String[] firstString = x.split(" ");
    String[] secondString = y.split(" ");
    String concatString1 = "";
    String concatString2 = "";
    for (String i : firstString) { // removing spaces and concatinating strings
      concatString1 += i;
    }
    for (String i : secondString) { // removing spaces and concatinating strings
      concatString2 += i;
    }
    for (int i = 0; i < concatString1.length(); i++) {
      insert(concatString1.charAt(i), 1);
    }
    for (int i = 0; i < concatString2.length(); i++) {
      if (!searchAndRemove(concatString2.charAt(i))) {
        return false;
      }
    }
    for (int i = 0; i < tableSize; i++) {
      if (table[i] != null) {
        return false;
      }
    }
    return true;
  }

  public static int find(String s, char c) {
    if (s.length() == 0) {
      return 0;
    }
    if (s.charAt(0) == c) {
      return 1 + find(s.substring(1), c);
    }
    return find(s.substring(1), c);
  }

  public static void main(String[] args) {
    // HashTable table = new HashTable(1);
    // System.out.println("Testing 'google' and 'trueasdf': " + table.checkIfAnagram("google", "trueasdf"));
    // System.out.println("Testing 'cowboy' and 'oybowc': " + table.checkIfAnagram("cowboy", "oybowc"));
    // System.out.println("Testing 'strawberry' and 'berrystraw': " + table.checkIfAnagram("strawberry", "berrystraw"));
    // System.out.println("Testing cases with 'asdfghjkl' and 'laksjdhfG': " + table.checkIfAnagram("asdfghjkl", "laksjdhfG"));
    // System.out.println("Testing cases with 'aSdfGhjkl' and 'lakSjdhfG': " + table.checkIfAnagram("aSdfGhjkl", "lakSjdhfG"));
    // System.out.println("Testing space removal with 'hello there sir' + 'she lloir ereht': " + table.checkIfAnagram("hello there sir", "she lloir ereht"));
    System.out.println(find("m", 'm'));
  }
}
