import java.util.ArrayList;
import java.util.Iterator;

import tester.Tester;

import java.util.NoSuchElementException;

//represents a collection of elements that interact with one element at a time
interface IWorkList<T> {
  // Adds the given element to the work list
  void add(T item);

  // Are there any elements in the work list?
  boolean isEmpty();

  // Returns the next element in the work list
  // EFFECT: Must remove the element returned
  T next();

  // empties this worklist
  void clear();
}

//represents a stack data structure, where the last items added are the first to be removed
class Stack<T> implements IWorkList<T> {
  private final Deque<T> contents;

  // Default constructor initializes empty collection of elements
  Stack() {
    this.contents = new Deque<T>();
  }

  // adds an item to the head of the list
  public void add(T item) {
    this.contents.addAtHead(item);
  }

  // determines if this stack contains no elements
  public boolean isEmpty() {
    return this.contents.size() == 0;
  }

  // Removes and returns the most recent element added, throwing exception if no elements
  // EFFECT: Modifies this' contents to not include the element returned
  public T next() {
    if (this.isEmpty()) {
      throw new NoSuchElementException("No more elements in worklist.");
    }
    return this.contents.removeFromHead();
  }

  // clears this stack
  public void clear() {
    while (! this.isEmpty()) {
      this.next();
    }
  }
}

//represents a queue data structure, where the first items added are the first to be removed
class Queue<T> implements IWorkList<T> {
  private final Deque<T> contents;

  // Default constructor initializes empty collection of elements
  Queue() {
    this.contents = new Deque<T>();
  }

  // adds an item to the tail of the list
  public void add(T item) {
    this.contents.addAtTail(item);
  }

  // determines if this queue contains no elements
  public boolean isEmpty() {
    return this.contents.size() == 0;
  }

  // Removes and returns the least recent element added, throwing exception if no elements
  // EFFECT: Modifies this' contents to not include the element returned
  public T next() {
    if (this.isEmpty()) {
      throw new NoSuchElementException("No more elements in worklist.");
    }
    return this.contents.removeFromHead();
  }

  // clears this queue
  public void clear() {
    while (! this.isEmpty()) {
      this.next();
    }
  }
}

class Utils {
  // Returns a reversed ArrayList of the given source
  <T> ArrayList<T> reverse(ArrayList<T> source) {
    Stack<T> tempStack = new Stack<T>();

    // Copy the source to the Stack in reverse order
    for(T item : source) {
      tempStack.add(item);
    }

    ArrayList<T> reversed = new ArrayList<T>();

    // Add every element of the Stack to the reversed array list
    // Uses for-each because there are the same number of elements in source as the created stack
    for(T item : source) {
      reversed.add(tempStack.next());
    }

    return reversed;
  }
}