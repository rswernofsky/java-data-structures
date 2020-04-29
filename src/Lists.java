/*
 * Around 80% of this was copied from the lecture notes for Northeastern's CS2510 course, written
 * by Benjamin Lerner (blerner@ccs.neu.edu)
 */

//represents a list of a generic class type that can be visited by a list visitor
interface IList<T> { 

  // accept this list by the given list visitor
  <R> R accept(IListVisitor<T, R> f);

  // applies the given function to every element of this list
  <U> IList<U> map(IFunc<T, U> f);

  // returns the length of this list
  int length();

  // applies a function to each successive element of this list, starting with the base
  <U> U foldr(IFunc2<T, U, U> func, U base);

  // returns whether or not any of the elements in this list fulfill the truth condition of the
  // given IPred
  boolean ormap(IPred<T> func);

  // adds the given list to the end of this list
  IList<T> append(IList<T> that);

  // add the given element to the end of this IList
  IList<T> add(T that);
  
  // returns a filtered version of this IList inlcuding only elements that satisfy
  // the given predicate
  IList<T> filter(IPred<T> pred); 
  
  // does this IList contain the given element?
  boolean contains(T that);
}


//represents a list of a generic class type
class MtList<T> implements IList<T> {

  // visits the given IListVisitor
  public <R> R accept(IListVisitor<T, R> f) {
    return f.visitMtList(this);
  }

  // applies the given function to every element of this empty list
  // returns an empty list because there's nothing to apply this function to
  public <U> IList<U> map(IFunc<T, U> f) {
    return new MtList<U>();
  }

  // returns the length of this empty list, which is 0
  public int length() {
    return 0; 
  }

  // applies a function to each successive element of this list, starting with the base
  public <U> U foldr(IFunc2<T, U, U> func, U base) {
    return base;
  }

  // returns whether or not any of the elements in this list fulfill the truth condition of the
  // given IPred. Returns false because this list is empty, so none of the elements can
  // verify a condition
  public boolean ormap(IPred<T> func) {
    return false;
  }

  // adds the given list to the end of this empty list
  public IList<T> append(IList<T> that) {
    return that;
  }
  
  // adds the given element to the end of this IList
  public IList<T> add(T that) {
    return new ConsList<T>(that, this);
  }

  // returns a filtered version of this MtList inlcuding only elements that satisfy
  // the given predicate
  // returns an MtList because this list contains no elements
  public IList<T> filter(IPred<T> pred) { 
    return this; 
  }
  
  // does this MtList contain the given element?
  // false because an empty list contains nothing
  public boolean contains(T that) {
    return false;
  }
}

//represents a list of a generic class type
class ConsList<T> implements IList<T> {
  // the first element in this non-empty list
  T first;

  // a list containing the rest of the elements in this non-empty list
  IList<T> rest;

  // constructor
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // visits the given IListVisitor
  public <R> R accept(IListVisitor<T, R> f) {
    return f.visitConsList(this);
  }

  // applies the given function to every element of this non-empty list
  public <U> IList<U> map(IFunc<T, U> f) {
    return new ConsList<U>(f.apply(this.first), this.rest.map(f));
  }

  // returns the number of elements in this non-empty list
  public int length() {
    return 1 + this.rest.length();
  }

  // applies a function to each successive element of this list, starting with the base
  public <U> U foldr(IFunc2<T, U, U> func, U base) {
    return func.apply(this.first,
        this.rest.foldr(func, base));
  }

  // returns whether or not any of the elements in this list fulfill the truth condition of the
  // given IPred
  public boolean ormap(IPred<T> func) {
    return func.apply(this.first) || this.rest.ormap(func);
  }

  // adds the given list to the end of this nonempty list
  public IList<T> append(IList<T> that) {
    return new ConsList<T>(this.first, this.rest.append(that));
  }

  // adds the given element to the end of this ConsList
  public IList<T> add(T that) {
    return new ConsList<T>(this.first, this.rest.add(that));
  }
  
  // returns a filtered version of this MtList inlcuding only elements that satisfy
  // the given predicate
  public IList<T> filter(IPred<T> pred) {
    if (pred.apply(this.first)) {
      return new ConsList<T>(this.first, this.rest.filter(pred));
    }
    else {
      return this.rest.filter(pred);
    }
  }
  
  // does this MtList contain the given element?
  // false because an empty list contains nothing
  public boolean contains(T that) {
    return this.first == that || this.rest.contains(that);
    // NOTE: this uses REFERENCE EQUALITY to determine sameness
  }
}


//represents a function that takes in one input and provides one output
//(both of which are of generic type)
interface IFunc<A, R> {

  // apply this function to the given argument
  R apply(A arg);
}


//Interface for two-argument function-objects with signature [A1, A2 -> R]
interface IFunc2<A1, A2, R> {

  // apply this function to the given arguments
  R apply(A1 arg1, A2 arg2);
}

//represents an IFunc which specifically returns a boolean
interface IPred<T> extends IFunc<T, Boolean> { }

//represents a visitor that can be applied to generic lists and returns an output of a single type
interface IListVisitor<T, R> extends IFunc<IList<T>, R> {

  // apply this visitor to an empty list
  R visitMtList(MtList<T> t);

  // apply this visitor to a non-empty (cons) list
  R visitConsList(ConsList<T> t);
}
