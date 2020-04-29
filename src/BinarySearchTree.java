import java.util.ArrayList;
import java.util.Iterator;
import tester.*;

// a contract for items that can be compared
interface IComparator<T> { 
  int compare(T left, T right); 
} 

// a contract for function objects
interface IFunc<A, R> { 

  // applies this function object to the given argument
  R apply(A arg); 
}

// a function object that acts on IBinTrees, using double dispatch to
// perform operations on nodes and leaves of binary trees uniquely
interface BinTreeVisitor<T, R> extends IFunc<IBinTree<T>, R> {

  // perform this function on a node of a binary tree
  R visitNode(Node<T> node);

  // perform this function on a leaf of a binary tree
  R visitLeaf(Leaf<T> leaf);
}

/*
 * Reasoning for avoiding mutation in Insert: 
 * When we use double dispatch to handle nodes and leaves differently, and we get down to the leaf
 * where we want to insert a new node at, in order to use mutation, the leaf now needs to be able 
 * to access its parent node in order to modify the parent node's left/right child to be something
 * other than the leaf we're currently looking at. This doesn't work since the leaf has no
 * knowledge of its parent node, so instead, we would need to think one step ahead when going 
 * down through the nodes, checking to see if the children in the direction of the path we need
 * to insert at are leaves of nodes. This doesn't take advantage of double dispatch anymore.
 * 
 * I decided to not use mutation so that I could instead, when reaching a leaf where we want to
 * insert a new node, return a new created node. Nodes can just recreate themselves with a
 * recursive use of the Insert function object on either their left or right branches based
 * on how the value that's being inserted compares to the node's value. 
 */


// insert a given value into a binary search tree using the given comparator
class Insert<T> implements BinTreeVisitor<T, IBinTree<T>> {

  // the value to be inserted into the BST
  T item;

  // a comparator for determining the order the value should be placed in the tree
  IComparator<T> order;

  // constructor
  Insert(T item, IComparator<T> order) {
    this.item = item;
    this.order = order;
  }

  // return the resulting binary tree from inserting the value into the given binary tree
  // after dynamically dispatching this function object to the specific type of IBinTree that's given
  public IBinTree<T> apply(IBinTree<T> bt) {
    return bt.accept(this);
  }

  // insert a value into a binary tree whose root is a node
  public IBinTree<T> visitNode(Node<T> node) {
    int comparison = this.order.compare(node.value, this.item);

    IBinTree<T> left = node.left;
    IBinTree<T> right = node.right;

    if (comparison > 0) { // the node's value is larger than the given item
      left = this.apply(node.left);
    } else if (comparison < 0) { // the node's value is smaller than the given item
      right = this.apply(node.right);
    } 
    // else case: (comparison == 0) --> don't change the left or right values

    return new Node<T>(node.value, left, right);
  }

  // insert a value into a binary tree whose root is a leaf
  // since it's a leaf, insert the value in this spot, creating a new node to replace the leaf
  public IBinTree<T> visitLeaf(Leaf<T> leaf) {
    return new Node<T>(this.item, new Leaf<T>(), new Leaf<T>());
  }
}

// return an integer representing the number of nodes in the given binary tree
class CountNodes<T> implements BinTreeVisitor<T, Integer> {

  // return an integer representing the number of nodes in the given binary tree
  public Integer apply(IBinTree<T> bt) {
    return bt.accept(this);
  }

  // if the given binary tree's root is a node, return 1 + the count of the left and right 
  // subtrees
  public Integer visitNode(Node<T> node) {
    return 1 + this.apply(node.left) + this.apply(node.right);
  }

  // if the given binary tree's root is a leaf, return 0
  public Integer visitLeaf(Leaf<T> leaf) {
    return 0;
  }
}

// similar to a "guess a number" game, this function object compares the given value
// to its stored value using a comparator, returning a value that informs whether the 
// given guess was too low, high, or correct
class CompareToAnswer<T> implements IFunc<T, Integer> {

  // a comparator for determining if the guess is lower, equal to, or higher
  // than the stored answer
  IComparator<T> order;

  // the correct value
  T answer;

  // constructor
  CompareToAnswer(IComparator<T> order, T item) {
    this.order = order;
    this.answer = item;
  }

  // compares the given guess with the stored answer using the stored comparator object
  // returns zero if that guess is correct
  // returns a negative value if the guess is smaller than the correct answer
  // returns a positive value if the guess is larger than the correct answer
  public Integer apply(T guess) {
    return this.order.compare(guess, this.answer);
  }
}

// uses a function that compares a value to its internal value to search through the
// given binary search tree to find the correct value
class Find<T> implements BinTreeVisitor<T, T> {
  // the comparison function that takes in a single value and returns whether the value
  // is lower, equal to, or higher than the stored value it has
  IFunc<T, Integer> search;

  // constructor
  Find(IFunc<T, Integer> search) {
    this.search = search;
  }

  // dynamically dispatches this function object to the specific type of IBinTree that's given
  public T apply(IBinTree<T> bt) {
    return bt.accept(this);
  }

  // uses the comparison function to see determine where to recursively call this function object
  // based on the value of the current node
  public T visitNode(Node<T> node) {
    int comparison = this.search.apply(node.value);

    if (comparison > 0) { // the current node's value is larger than the correct value
      // search for the correct value in the nodes that contain smaller values than 
      // this node's value
      return this.apply(node.left); 
    } else if (comparison < 0) {// the current node's value is smaller than the correct value
      // search for the correct value in the nodes that contain larger values than 
      // this node's value
      return this.apply(node.right);
    } else { // the current node's value is the correct value
      return node.value;
    }
  }

  // if we've reached a leaf, the T value isn't contained within the given binary tree
  public T visitLeaf(Leaf<T> leaf) {
    return null;
  }
}

// get the corresponding t value at the given index
class GetValueAtIndex<T> implements BinTreeVisitor<T, T> {

  // the index to get the value of
  int index;

  // constructor
  GetValueAtIndex(int index) {
    this.index = index;
  }

  // dynamically dispatches this function object to the specific type of IBinTree that's given
  public T apply(IBinTree<T> bt) {
    return bt.accept(this);
  }

  // if the current node is the correct index (as calculated), return it. otherwise, recursively
  // get the index of the left or right subtree using a different index for the right subtree
  // EFFECT: index becomes the index of the subtree when recursively called
  public T visitNode(Node<T> node) {
    // the number of nodes in the binary tree to the left of the current node
    int leftCount = new CountNodes<T>().apply(node.left);

    if (leftCount > this.index) {
      return this.apply(node.left);
    } else if (leftCount == this.index) {
      return node.value;
    } else { // leftCount < index --> answer will be in right side
      this.index -= leftCount + 1;
      return this.apply(node.right);
    }
  }

  // a leaf will only be visited if the index is less than 0 or greater
  // than the number of nodes in the bst - 1, so throw an error
  public T visitLeaf(Leaf<T> leaf) {
    throw new RuntimeException("The given index is out of bounds for this BST.");
  }
}


// represents a binary search tree, which has the invariant that nodes to the left of any node
// have a value that's "smaller" than the node's value, and nodes to the right have a value that's
// "larger"
// contains no duplicate values
class BinarySearchTree<T> implements Iterable<T> {

  // the comparator for the values of the nodes in this bst
  IComparator<T> order;

  // the root node in this bst
  IBinTree<T> root;

  // constructor
  // sets the root node to a leaf (empty)
  BinarySearchTree(IComparator<T> order) { 
    this.order = order; 
    this.root = new Leaf<T>();
  }

  // EFFECT: inserts the given item into this binary search tree according to the comparator
  void insert(T item) {
    this.root = new Insert<T>(item, this.order).apply(this.root);
  }

  // returns the value at the given node when the given search returns zero
  // returns null if there's no value in the tree that satisfies the search
  T find(IFunc<T, Integer> search) {
    return new Find<T>(search).apply(this.root);
  }

  // returns the number of nodes in this binary tree
  int size() {
    return new CountNodes<T>().apply(this.root);
  }

  // returns the item contained within this binary search tree at the given index
  // throws an error if the given index is >= the size of this tree
  T get(int index) {
    return new GetValueAtIndex<T>(index).apply(this.root);
  }

  // returns an iterator that can repeatedly get the next value of this bst
  public Iterator<T> iterator() {
    return new InOrderBinTreeIterator<T>(this);
  }
}

// an iterator for BSTs, which gets the next value of a bst based on index
class InOrderBinTreeIterator<T> implements Iterator<T> {

  // the binary search tree
  BinarySearchTree<T> bst;

  // the index of the node we're currently at
  int index;
  
  // the number of nodes in this BST
  int size;

  // constructor
  InOrderBinTreeIterator(BinarySearchTree<T> bst) {
    this.bst = bst;
    this.index = 0;
    this.size = this.bst.size();
  }

  // is there a next value in the bst?
  public boolean hasNext() {
    return this.index < this.size;
  }

  // returns the value at the next index of the bst, if there is one
  // errors if there is not a next value
  // EFFECT: increments the index of the node we're currently at
  public T next() {
    if (this.hasNext()) {
      T data = this.bst.get(this.index);
      this.index += 1;
      return data;
    } else {
      throw new RuntimeException("The binary search tree has no more values!");
    }
  }
}


// represents a binary tree structure
interface IBinTree<T> { 

  // dispatch to a function object visitor
  <R> R accept(BinTreeVisitor<T, R> f);
}

// represents a node in a binary tree that contains a value
class Node<T> implements IBinTree<T> {

  // the value that this node stores
  T value;

  // the left and right subtrees (can be nodes or leaves)
  IBinTree<T> left, right;

  // constructor
  Node(T val, IBinTree<T> left, IBinTree<T> right) {
    this.value = val; 
    this.left = left; 
    this.right = right;
  } 

  // dispatch to a function object visitor with this node
  public <R> R accept(BinTreeVisitor<T, R> f) {
    return f.visitNode(this);
  }
}

// represents a leaf of a binary tree - it contains no value
class Leaf<T> implements IBinTree<T> { 

  // dispatch to a function object visitor with this leaf
  public <R> R accept(BinTreeVisitor<T, R> f) {
    return f.visitLeaf(this);
  }
}

// an example comparator, which compares integers in such a way that listing them from
// "smallest" to "largest" will list them in incrementing order
class IncrementingIntegers implements IComparator<Integer> {

  // compares two integers. a negative result means the first value is "smaller"
  // a positive result means the first value is "larger", and a 0 result means they're equal
  public int compare(Integer left, Integer right) {
    return left - right;
    // if left > right, the answer will be positive
    // if left < right, the answer will be negative
    // if left == right, the answer will be 0
  }
}

// an example class for testing binary search tree functionality
class ExamplesBinaryTree {
  // a binary search tree of integers that contains no elements
  BinarySearchTree<Integer> bstIntTiny;

  // a binary search tree of integers that contains a few elements (4 elements)
  BinarySearchTree<Integer> bstIntSmall;

  // a binary search tree of integers that contains a lot of elements (14 elements)
  BinarySearchTree<Integer> bstIntLarge;

  // comparator that compares integers where smaller integers are less than larger integers
  IncrementingIntegers incInts;

  // initializes all the example data
  void initData() {
    // initialize the incrementing integers comparator
    this.incInts = new IncrementingIntegers();

    // initialize a binary search tree of integers that contains only a leaf
    this.bstIntTiny = new BinarySearchTree<>(incInts);

    // initialize a binary search tree of integers that contains 4 elements
    this.bstIntSmall = new BinarySearchTree<>(incInts);
    this.bstIntSmall.insert(6);
    this.bstIntSmall.insert(2);
    this.bstIntSmall.insert(9);
    this.bstIntSmall.insert(3);

    // initialize a binary search tree of integers that contains only a leaf
    this.bstIntLarge = new BinarySearchTree<>(incInts);
    this.bstIntLarge.insert(7);
    this.bstIntLarge.insert(11);
    this.bstIntLarge.insert(9);
    this.bstIntLarge.insert(10);
    this.bstIntLarge.insert(3);
    this.bstIntLarge.insert(5);
    this.bstIntLarge.insert(4);
    this.bstIntLarge.insert(1);
    this.bstIntLarge.insert(2);
    this.bstIntLarge.insert(0);
    this.bstIntLarge.insert(4);
    this.bstIntLarge.insert(6);
    this.bstIntLarge.insert(8);
    this.bstIntLarge.insert(77);
  }


  // test that inserting new data into a binary search tree
  //  - increases the size of the tree
  //  - gets inserted at the right place
  void testInsertIntoEmptyBST(Tester t) {
    this.initData();

    // ensure that the current number of nodes in the empty bst is 0
    t.checkExpect(this.bstIntTiny.size(), 0);

    // insert the integer 6 into the bst
    this.bstIntTiny.insert(6);

    // ensure that the current number of nodes in the empty bst is 0
    t.checkExpect(this.bstIntTiny.size(), 1);

    // make sure that inserting values that are "smaller" than the root's value
    // places them to the left
    this.bstIntTiny.insert(3);
    t.checkExpect(((Node<Integer>)((Node<Integer>)this.bstIntTiny.root).left).value, 3);

    // make sure that inserting values that are "larger" than the root's value
    // places them to the right
    this.bstIntTiny.insert(8);
    t.checkExpect(((Node<Integer>)((Node<Integer>)this.bstIntTiny.root).right).value, 8);

    // make sure that inserting values that are in between the values of the root and
    // the root's child places them correctly
    this.bstIntTiny.insert(4);
    t.checkExpect(((Node<Integer>)((Node<Integer>)((Node<Integer>)this.bstIntTiny.root)
        .left).right).value, 4);
  }

  // test finding an integer within a bst that contains that integer
  void testFindExistingNumber(Tester t) {
    this.initData();

    // a comparator that compares a given integer to 9 to see if it's smaller, the same, 
    // or larger
    CompareToAnswer<Integer> compareTo9 = new CompareToAnswer<>(this.incInts, 9);

    // 9 is already in bstIntSmall, so finding it with compareTo9 will return 9
    t.checkExpect(this.bstIntSmall.find(compareTo9), 9);
  }

  // test finding an integer within a bst that *does not* contain that integer
  void testFindNonexistantNumber(Tester t) {
    this.initData();

    // a comparator that compares a given integer to 9 to see if it's smaller, the same, 
    // or larger
    CompareToAnswer<Integer> compareTo9 = new CompareToAnswer<>(this.incInts, 9);

    // 9 is not in bstIntTiny, since bstIntTiny has no elements, so finding it 
    // with compareTo9 will return 9
    t.checkExpect(this.bstIntTiny.find(compareTo9), null);

    // if we insert 9 into bstIntTiny, it should now be able to be found
    this.bstIntTiny.insert(9);
    t.checkExpect(this.bstIntTiny.find(compareTo9), 9);



    // bstIntLarge contains lots of elements, but 420 is not one of them, 
    // so searching for 420

    // a comparator that compares a given integer to 9 to see if it's smaller, the same, 
    // or larger
    CompareToAnswer<Integer> compareTo420 = new CompareToAnswer<>(this.incInts, 420);

    t.checkExpect(this.bstIntLarge.find(compareTo420), null);
  }

  // test getting data from the bst at an index
  void testGet(Tester t) {
    this.initData();

    // test getting the value in the bst at the root's index
    t.checkExpect(this.bstIntSmall.get(2), 6);

    // test getting the value in the bst at an index to the left of the root
    t.checkExpect(this.bstIntSmall.get(1), 3);

    // test getting the value in the bst at an index to the right of the root
    t.checkExpect(this.bstIntSmall.get(3), 9);

    // test getting the value in the bst at an index that's out of bounds 
    // where it's negative - too small
    //    t.checkExpect(bstIntSmall.get(-1), an error);
    t.checkException(new RuntimeException("The given index is out of bounds for this BST."),
        this.bstIntSmall, "get", -1);

    // test getting the value in the bst at an index that's out of bounds (too large)
    t.checkException(new RuntimeException("The given index is out of bounds for this BST."),
        this.bstIntSmall, "get", 77);
  }

  // test the behavior of the bst iterator
  void testIterateOverBST(Tester t) {
    this.initData();

    // test that the iterator produces the correct number of values, and 
    // they're in the correct sequence
    ArrayList<Integer> bstIntSmallData = new ArrayList<>();
    ArrayList<Integer> dataShouldBe = new ArrayList<>();

    dataShouldBe.add(2);
    dataShouldBe.add(3);
    dataShouldBe.add(6);
    dataShouldBe.add(9);

    for (Integer data : this.bstIntSmall) {
      bstIntSmallData.add(data);
    }

    t.checkExpect(bstIntSmallData, dataShouldBe);

  }
}