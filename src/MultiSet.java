import tester.*;

// A generic pair of values. You may access its fields directly
// (similar to Posn), but you may not add any methods to this class
class Pair<X, Y> {

  // the first element of the pair
  X first;

  // the second element of the pair
  Y second;

  // constructor
  Pair(X first, Y second) { 
    this.first = first; 
    this.second = second; 
  }
}

// A basic interface to a multi-set of values of type T. No guarantees are
// made about the order of values retrieved during iteration over this multi-set. 
interface IMultiSet<T> extends Iterable<Pair<T, Integer>> {

  // How often does the given item appear in this multi set?
  // (If it is not present, then return 0.)
  int itemCount(T item);

  // The given multiset is a subset of this multiset if every item 
  // in the given multiset is present in this one, with a count
  // that’s less than or equal to the count in this multiset.
  boolean hasSubset(IMultiSet<T> other);

  // EFFECT: adds the given item into this multiset
  void add(T item);
}

// a comparator for pairs that compares just their first elements using the given value comparator
class ComparePairs<T> implements IComparator<Pair<T, Integer>> {

  // the comparator for the values of the pairs
  IComparator<T> valueComparator;

  // constructor
  ComparePairs(IComparator<T> valueComparator) {
    this.valueComparator = valueComparator;
  }

  // compare the pairs by comparing their first elements with the value comparer
  public int compare(Pair<T, Integer> left, Pair<T, Integer> right) {
    return valueComparator.compare(left.first, right.first);
  }
}

// similar to a "guess a number" game, this function object compares the given *pair's* value
// to its stored value using a comparator, returning a value that informs whether the 
// given guess was too low, high, or correct
class ComparePairToAnswer<T> implements IFunc<Pair<T, Integer>, Integer> {

  // a comparator for determining if the guess is lower, equal to, or higher
  // than the stored answer
  IComparator<T> order;

  // the correct value
  T answer;

  // constructor
  ComparePairToAnswer(IComparator<T> order, T item) {
    this.order = order;
    this.answer = item;
  }

  // compares the given guess with the stored answer using the stored comparator object
  // returns zero if that guess is correct
  // returns a negative value if the guess is smaller than the correct answer
  // returns a positive value if the guess is larger than the correct answer
  public Integer apply(Pair<T, Integer> guess) {
    return this.order.compare(guess.first, this.answer);
  }
}


//Allows creating sets of values where the elements of the set are *comparable*.
class OrderableMultiSet<T> extends BinarySearchTree<Pair<T, Integer>> implements IMultiSet<T> {

  // comparator of values of pairs in this orderable multiset
  IComparator<T> tOrder;

  // constructor
  // constructs a binary search tree with a new comparator for pairs based on the given comparator
  // of values
  OrderableMultiSet(IComparator<T> comp) { 
    super(new ComparePairs<T>(comp));
    this.tOrder = comp;
  }

  // How often does the given item appear in this multi set?
  // (If it is not present, then return 0.)
  public int itemCount(T item) {
    CompareToAnswer<Pair<T, Integer>> comparer = new CompareToAnswer<Pair<T, Integer>>(this.order, 
        new Pair<T, Integer>(item, 1)); // TODO: understand how I did this
    Pair<T, Integer> pair = new Find<Pair<T, Integer>>(comparer).apply(this.root);
    // also could have just done: this.find(comparer);

    if (pair != null) {
      return pair.second;
    } else {
      return 0;
    }
  }

  // The given multiset is a subset of this multiset if every item in the given multiset is 
  // present in this one, with a count that’s less than or equal to the count in this multiset.
  public boolean hasSubset(IMultiSet<T> other) {
    
    for (Pair<T, Integer> pair : other) {
      if (pair.second > this.itemCount(pair.first)) {
        return false;
      }
    }

    return true;
  }

  // EFFECT: adds the given item into this multiset
  public void add(T item) {
    ComparePairToAnswer<T> comparison = new ComparePairToAnswer<T>(this.tOrder, item);
    Pair<T, Integer> pair = this.find(comparison); // TODO: understand what I did here

    if (pair == null) {
      this.insert(new Pair<T, Integer>(item, 1));
    } else {
      pair.second += 1;
    }
  }

  //  public Iterator<T> iterator() {
  //    return new InOrderMultiSetTreeIterator<T>(this);
  //  }

  // is this orderable multiset equal to the given object?
  // two multisets are equal if they're both subsets of each other
  public boolean equals(Object other) {
    if (!(other instanceof OrderableMultiSet)) { 
      return false; 
    }
    // this cast is safe, because we just checked instanceof
    IMultiSet<T> that = (IMultiSet<T>) other;
    // return this.hashCode() == that.hashCode();
    return this.hasSubset(that) && that.hasSubset(this);
    // set equality
  }



  // returns a value that uniquely represents this orderable multiset
  // the value will be the same for equivalent multisets
  // computes the hashCode using a subset of the fields used to compute orderable multiset equality
  public int hashCode() {
    int hash = 0;

    // add the hash of each element, scaled by the amount of times it exists in this
    // multiset, to the hash
    for (Pair<T, Integer> pair : this) {
      hash += pair.first.hashCode() * pair.second;
    }

    return hash;
  }
}



// a wrapper class for an integer with a modified hashCode method
class CustomInt {
  // the integer value
  int value;

  // constructor
  CustomInt(int value) {
    this.value = value;
  }

  // returns a value that uniquely represents this integer
  public int hashCode() {
    return this.value;
  }

  // get this custom integer's integer value
  int getValue() {
    return this.value;
  }
}

// an example comparator, which compares integers in such a way that listing them from
// "smallest" to "largest" will list them in incrementing order
class IncrementingCustomIntegers implements IComparator<CustomInt> {

  // compares two integers. a negative result means the first value is "smaller"
  // a positive result means the first value is "larger", and a 0 result means they're equal
  public int compare(CustomInt left, CustomInt right) {
    return left.getValue() - right.getValue();
    // if left > right, the answer will be positive
    // if left < right, the answer will be negative
    // if left == right, the answer will be 0
  }
}

class SumMultiSet implements IFunc<OrderableMultiSet<CustomInt>, CustomInt> {
  public CustomInt apply(OrderableMultiSet<CustomInt> set) {
    int sum = 0;

    for (Pair<CustomInt, Integer> pair : set) {
      sum += pair.first.getValue();
    }

    return new CustomInt(sum);
  }
}

// an example comparator, which compares orderable multisets in such a way that listing them from
// "smallest" to "largest" will list them in incrementing order based on the sum of their elements
class IncrementingSumSets implements IComparator<OrderableMultiSet<CustomInt>> {

  // compares two integers. a negative result means the first value is "smaller"
  // a positive result means the first value is "larger", and a 0 result means they're equal
  public int compare(OrderableMultiSet<CustomInt> left, OrderableMultiSet<CustomInt> right) {
    SumMultiSet summer = new SumMultiSet();
    return summer.apply(left).getValue() - summer.apply(right).getValue();
    // if left > right, the answer will be positive
    // if left < right, the answer will be negative
    // if left == right, the answer will be 0
  }
}



// a class for testing ordered multiset functionality
class ExamplesMultiSet {

  IncrementingCustomIntegers incInts;
  OrderableMultiSet<CustomInt> mtSet;
  OrderableMultiSet<CustomInt> smallSet;
  OrderableMultiSet<CustomInt> reorderedSmallSet;
  OrderableMultiSet<CustomInt> smallSetWithOverlap;
  OrderableMultiSet<CustomInt> bigSet;

  // initialize the example data
  void initData() {
    this.incInts = new IncrementingCustomIntegers();
    this.mtSet = new OrderableMultiSet<CustomInt>(this.incInts);
    this.smallSet = new OrderableMultiSet<CustomInt>(this.incInts);
    this.smallSet.add(new CustomInt(6));
    this.smallSet.add(new CustomInt(2));
    this.smallSet.add(new CustomInt(9));
    this.smallSet.add(new CustomInt(3));

    this.reorderedSmallSet = new OrderableMultiSet<CustomInt>(this.incInts);
    this.reorderedSmallSet.add(new CustomInt(2));
    this.reorderedSmallSet.add(new CustomInt(3));
    this.reorderedSmallSet.add(new CustomInt(6));
    this.reorderedSmallSet.add(new CustomInt(9));

    this.smallSetWithOverlap = new OrderableMultiSet<CustomInt>(this.incInts);
    this.smallSetWithOverlap.add(new CustomInt(6));
    this.smallSetWithOverlap.add(new CustomInt(2));
    this.smallSetWithOverlap.add(new CustomInt(9));
    this.smallSetWithOverlap.add(new CustomInt(3));
    this.smallSetWithOverlap.add(new CustomInt(8));
    this.smallSetWithOverlap.add(new CustomInt(8));

    this.bigSet = new OrderableMultiSet<CustomInt>(this.incInts); 
    this.bigSet.add(new CustomInt(7));
    this.bigSet.add(new CustomInt(11));
    this.bigSet.add(new CustomInt(9));
    this.bigSet.add(new CustomInt(10));
    this.bigSet.add(new CustomInt(3));
    this.bigSet.add(new CustomInt(5));
    this.bigSet.add(new CustomInt(4));
    this.bigSet.add(new CustomInt(1));
    this.bigSet.add(new CustomInt(2));
    this.bigSet.add(new CustomInt(0));
    this.bigSet.add(new CustomInt(4));
    this.bigSet.add(new CustomInt(6));
    this.bigSet.add(new CustomInt(8));
    this.bigSet.add(new CustomInt(77));


  }

  // test whether adding an element to an orderable multiset will add a new node only when
  // a node doesn't already exist with the same pair value, and if it will increment the count
  // of a node's pair when it does exist
  void testAdd(Tester t) {
    this.initData();

    // test adding an element to a set that doesn't already have it should create a new node
    // mtSet has no elements, so adding anything should create a new node in the tree representing the set
    t.checkExpect(this.mtSet.size(), 0);
    this.mtSet.add(new CustomInt(6));
    t.checkExpect(this.mtSet.size(), 1);
    t.checkExpect(this.mtSet.get(0).first.getValue(), 6);
    t.checkExpect(this.mtSet.get(0).second, 1);

    // test adding an element that's already in the set should just increase the count of that element's pair
    // mtSet now has one node containing the value 6, so adding another 6 should increase the pair's count
    this.mtSet.add(new CustomInt(6));
    t.checkExpect(this.mtSet.size(), 1);
    t.checkExpect(this.mtSet.get(0).first.getValue(), 6);
    t.checkExpect(this.mtSet.get(0).second, 2);
  }

  // test counting the number of elements there are that have a certain value in various 
  // different sets
  void testCount(Tester t) {
    this.initData();
    // test counting the number of elements in a set with none of that element
    t.checkExpect(this.smallSet.itemCount(new CustomInt(5)), 0);

    // test counting the number of elements in a set with one of that element
    t.checkExpect(this.smallSet.itemCount(new CustomInt(6)), 1);

    // test counting the number of elements in a set with multiple of that element (and inserted in random orders)
    t.checkExpect(this.smallSetWithOverlap.itemCount(new CustomInt(8)), 2);
  }

  // test whether various sets are subsets of others
  void testSubset(Tester t) {
    this.initData();
    // test whether an empty set is a subset of a set with elements in it
    t.checkExpect(this.bigSet.hasSubset(this.mtSet), true);

    // test whether an empty set is a subset of an empty set
    t.checkExpect(this.mtSet.hasSubset(this.mtSet), true);

    // test whether a set with a few elements in it is a subset of another set with a few elements in it
    t.checkExpect(this.smallSetWithOverlap.hasSubset(this.smallSet), true);

    // test whether a set with elements in it is a subset of an empty set
    t.checkExpect(this.mtSet.hasSubset(this.bigSet), false);

    // test whether a set with elements in it is a subset of a set with fewer elements in it
    t.checkExpect(this.smallSet.hasSubset(this.smallSetWithOverlap), false);

    // test whether ordering matters when checking subsets
    t.checkExpect(this.smallSet.hasSubset(this.reorderedSmallSet), true);
    t.checkExpect(this.reorderedSmallSet.hasSubset(this.smallSet), true);

    // test whether a set with more of one element in it is a subset of a set with with the same element but fewer
    this.smallSet.add(new CustomInt(8)); // now smallSet has only one 8, but smallSetWithOverlap has two
    t.checkExpect(this.smallSetWithOverlap.hasSubset(this.smallSet), true);
    t.checkExpect(this.smallSet.hasSubset(this.smallSetWithOverlap), false);
  }

  // test whether sets are equal under the correct circumstances
  void testEquality(Tester t) {
    this.initData();

    // test whether two empty sets are equal?
    OrderableMultiSet<CustomInt> anotherMtSet = new OrderableMultiSet<CustomInt>(this.incInts);
    t.checkExpect(this.mtSet.equals(anotherMtSet), true);

    // test whether equality is reflexive
    t.checkExpect(anotherMtSet.equals(this.mtSet), true);

    // test whether two sets with different elements are equal
    t.checkExpect(this.smallSet.equals(this.smallSetWithOverlap), false);

    // test whether two sets with the same elements, but that are ordered differently are equal
    t.checkExpect(this.smallSet.equals(this.reorderedSmallSet), true);
  }

  // test whether multisets of multisets are equal
  void testSetofSetEquality(Tester t) {
    this.initData();

    // test whether two multisets that are reordered versions of each other are equal
    IncrementingSumSets incSums = new IncrementingSumSets();

    OrderableMultiSet<OrderableMultiSet<CustomInt>> inception = new OrderableMultiSet<>(incSums);
    inception.add(this.smallSet);
    inception.add(this.bigSet);

    OrderableMultiSet<OrderableMultiSet<CustomInt>> inception2 = new OrderableMultiSet<>(incSums);
    inception2.add(this.bigSet);
    inception2.add(this.smallSet); // added in a different order

    t.checkExpect(inception.equals(inception2), true);
  }

  // test whether equal sets have the same hash code, and if different sets have different codes
  void testHashCode(Tester t) {
    this.initData();

    // test if equal sets have the same hash code
    t.checkExpect(this.smallSet.equals(this.reorderedSmallSet), true); // these two sets are equal
    t.checkExpect(this.smallSet.hashCode() == this.reorderedSmallSet.hashCode(), true);

    // test if non-equal sets have different hash codes
    t.checkExpect(this.smallSet.hashCode() == this.smallSetWithOverlap.hashCode(), false);
  }
}






