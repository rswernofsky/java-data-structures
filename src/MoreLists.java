import tester.Tester;

// a non-mutable list of strings
interface ILoString {
  //compute an integer representing the number of string elements within this ILoString
  int length();

  // produce the ILoString that results in reversing this ILoString
  ILoString reverse();

  // convert this ILoString into only a combination of ConsLoString and MtLoString lists
  // maintains original order of the elements in this ILoString
  ILoString normalize();

  // add the elements of the given ILoString, back, to the end of this ILoString, in their original 
  // order
  ILoString append(ILoString back);

  // produces an ILoString where each string element is all the prior strings (from this ILoString)
  // concatenated together from left to right
  // maintains original order of the elements in this ILoString
  ILoString scanConcat();

  // produces an ILoString where each string element is all the prior strings (from this ILoString)
  // concatenated together from left to right, using an accumulator which stores all the previous
  // strings (from this list, left to right) concatenated together
  // maintains original order of the elements in this ILoString
  ILoString scanHelper(String prevConcat);
}


// represents an empty list of strings
class MtLoString implements ILoString {

  // computes the length of this (an empty) ILoString
  public int length() {
    return 0;
  }

  // reverses the 0 elements in this (an empty) ILoString
  public ILoString reverse() {
    return this;
  }

  // converts this (an empty) ILoString into MtLoString format (which it already is in)
  public ILoString normalize() {
    return this;
  }

  // adds the given ILoString, back, to the end of this empty ILoString, in the correct order
  public ILoString append(ILoString back) {
    return back;
  }

  // returns a list of all of the 0 elements in this empty ILoString concatenated consecutively
  public ILoString scanConcat() {
    return this;
  }

  // produces a list of the 0 elements in this empty ILoString concatenated consecutively using 
  // an accumulator which accumulates all 0 strings in this ILoString from left to right
  public ILoString scanHelper(String prevConcat) {
    return this;
  }
}

// represents an ILosString with a string before it
class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  // the constructor
  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  // compute the number of string elements contained within this ConsLoString
  public int length() {
    return 1 + this.rest.length(); 
  }

  // produce the ILoString that results in reversing this ConsLoString
  public ILoString reverse() {
    return new SnocLoString(this.rest.reverse(), this.first);
  }

  // convert this ConsLoString into only a combination of ConsLoString and MtLoString lists
  // maintains original order of the elements in this ConsLoString 
  public ILoString normalize() {
    return new ConsLoString(this.first, this.rest.normalize());
  }

  // add the elements of the given ILoString, back, to the end of this ConsLoString, in their 
  // original order
  public ILoString append(ILoString back) {
    return new ConsLoString(this.first, this.rest.append(back));
  }

  // produces an ILoString where each string element is all the prior strings (from this 
  // ConsLoString) concatenated together from left to right
  // maintains original order of the elements in this ILoString
  public ILoString scanConcat() {
    return this.normalize().scanHelper("");
  }

  // produces an ILoString where each string element is all the prior strings (from this 
  // ConsLoString) concatenated together from left to right, using an accumulator which 
  // stores all the previous strings (from this list, left to right) concatenated together
  // maintains original order of the elements in this ILoString
  public ILoString scanHelper(String prevConcat) {
    String newConcat = prevConcat + this.first; 
    // we use this value multiple times, so we're storing it as a local variable
    return new ConsLoString(newConcat, this.rest.scanHelper(newConcat));
  }
}

// represents an ILosString with a string after it
class SnocLoString implements ILoString {
  ILoString front;
  String last;

  // the constructor
  SnocLoString(ILoString front, String last) {
    this.front = front;
    this.last = last;
  }

  // compute the number of string elements contained within this SnocLoString
  public int length() { 
    return this.front.length() + 1; 
  }

  // produce the ILoString that results in reversing this SnocLoString
  public ILoString reverse() {
    return new ConsLoString(this.last, this.front.reverse());
  }

  // convert this SnocLoString into only a combination of ConsLoString and MtLoString lists
  // maintains original order of the elements in this SnocLoString 
  public ILoString normalize() {
    return this.front.append(new ConsLoString(this.last, new MtLoString()));
  }

  // add the elements of the given ILoString, back, to the end of this SnocLoString, in their 
  // original order
  public ILoString append(ILoString back) {
    return this.front.append(new ConsLoString(this.last, back));
  }

  // produces an ILoString where each string element is all the prior strings (from this 
  // SnocLoString) concatenated together from left to right
  // maintains original order of the elements in this SnocLoString
  public ILoString scanConcat() {
    return this.scanHelper("");
  }

  // produces an ILoString where each string element is all the prior strings (from this 
  // SnocLoString) concatenated together from left to right, using an accumulator which stores 
  // all the previous strings (from this list, left to right) concatenated together
  // maintains original order of the elements in this SnocLoString
  public ILoString scanHelper(String prevConcat) { 
    return this.normalize().scanHelper(prevConcat);
  }
}

// represents two ILosStrings appended together
class AppendLoString implements ILoString {
  ILoString front;
  ILoString back;

  // the constructor
  AppendLoString(ILoString front, ILoString back) {
    this.front = front;
    this.back = back;
  }

  // compute the number of string elements contained within this AppendLoString
  public int length() { 
    return this.front.length() + this.back.length(); 
  }

  // produce the ILoString that results in reversing this AppendLoString
  public ILoString reverse() {
    return new AppendLoString(this.back.reverse(), this.front.reverse());
  }

  // convert this AppendLoString into only a combination of ConsLoString and MtLoString lists
  // maintains original order of the elements in this AppendLoString 
  public ILoString normalize() {
    return this.front.normalize().append(back.normalize());
  }

  // add the elements of the given ILoString, back, to the end of this AppendLoString, in their 
  // original order
  public ILoString append(ILoString back) {
    return new AppendLoString(this, back);
  }

  // produces an ILoString where each string element is all the prior strings (from this 
  // AppendLoString) concatenated together from left to right
  // maintains original order of the elements in this AppendLoString
  public ILoString scanConcat() {
    return this.scanHelper("");
  }

  // produces an ILoString where each string element is all the prior strings (from this 
  // AppendLoString) concatenated together from left to right, using an accumulator which stores 
  // all the previous strings (from this list, left to right) concatenated together
  // maintains original order of the elements in this AppendLoString
  public ILoString scanHelper(String prevConcat) {
    return this.normalize().scanHelper(prevConcat);
  }

}

class ExamplesILoString {
  // example of an empty list of strings
  MtLoString mt = new MtLoString();

  // examples of simple ConLoStrings
  ConsLoString cons1 = new ConsLoString("hey!", mt);
  ConsLoString cons2 = new ConsLoString("hola", cons1);
  ConsLoString cons3 = new ConsLoString("bonjour", cons2);

  // examples of simple SnocLoStrings
  SnocLoString snoc1 = new SnocLoString(mt, "hey!");
  SnocLoString snoc2 = new SnocLoString(snoc1, "Sean has cool hair");
  SnocLoString snoc3 = new SnocLoString(snoc2, "Reb is very smol");
  SnocLoString firstSnoc = new SnocLoString(new SnocLoString(new MtLoString(),"w"), "x");
  SnocLoString secondSnoc = new SnocLoString(new SnocLoString(new MtLoString(), "y"), "z");

  // examples of more complex ConLoStrings
  ConsLoString cons4 = new ConsLoString("hello", new SnocLoString(mt, "world"));
  ConsLoString cons5 = new ConsLoString("hat", new ConsLoString("warmth", snoc3));

  // examples of more complex SnocLoStrings
  SnocLoString snoc4 = new SnocLoString(cons1, "oh boy");
  SnocLoString snoc5 = new SnocLoString(cons4, "Lucky Charms are tasty");

  // examples of AppendLoString
  AppendLoString append1 = new AppendLoString(mt, mt); // an empty list, but in "empty" format
  AppendLoString append2 = new AppendLoString(cons1, mt);
  AppendLoString append3 = new AppendLoString(snoc2, cons3);
  AppendLoString append4 = new AppendLoString(snoc5, cons5);
  AppendLoString test  = new AppendLoString(
      new AppendLoString(
          new SnocLoString(
              new SnocLoString(
                  new MtLoString(),"w"), 
              "x"), 
          new SnocLoString(
              new SnocLoString(
                  new MtLoString(), "y"), 
              "z")), 
      new ConsLoString("a", new MtLoString()));

  // test the method length() for MtLoString, ConsLoString, SnocLoString, and AppendLoString
  boolean testLength(Tester t) {
    return t.checkExpect(mt.length(), 0)
        && t.checkExpect(cons3.length(), 3)
        && t.checkExpect(cons2.length(), 2)
        && t.checkExpect(append1.length(), 0)
        && t.checkExpect(append2.length(), 1)
        && t.checkExpect(snoc4.length(), 2)
        && t.checkExpect(snoc5.length(), 3)
        && t.checkExpect(append4.length(), 8);
  }

  // test the method reverse() for MtLoString, ConsLoString, SnocLoString, and AppendLoString
  boolean testReverse(Tester t) {
    return t.checkExpect(mt.reverse(), mt)
        && t.checkExpect(cons1.reverse(), snoc1)
        && t.checkExpect(cons3.reverse(), new SnocLoString(
            new SnocLoString(new SnocLoString(mt, "hey!"), "hola"), "bonjour"))
        && t.checkExpect(snoc2.reverse(), new ConsLoString("Sean has cool hair", 
            new ConsLoString("hey!", mt)))
        && t.checkExpect(snoc4.reverse(), new ConsLoString("oh boy", 
            new SnocLoString(mt, "hey!")))
        && t.checkExpect(append2.reverse(), new AppendLoString(mt, new SnocLoString(mt, "hey!")))
        && t.checkExpect(append4.reverse(), new AppendLoString(new SnocLoString(new SnocLoString(
            new ConsLoString("Reb is very smol", new ConsLoString("Sean has cool hair", 
                new ConsLoString("hey!", mt))), "warmth"), "hat"), 
            new ConsLoString("Lucky Charms are tasty", new SnocLoString(
                new ConsLoString("world", mt), "hello"))));
  }

  // test the method normalize() for MtLoString, ConsLoString, SnocLoString, and AppendLoString
  boolean testNormalize(Tester t) {
    return t.checkExpect(snoc1.normalize(), cons1)
        && t.checkExpect(snoc1.normalize(), cons1.normalize())
        && t.checkExpect(cons3.normalize(), cons3)
        && t.checkExpect(mt.normalize(), mt)
        && t.checkExpect(cons2.normalize(), cons2)
        && t.checkExpect(cons4.normalize(), new ConsLoString("hello", 
            new ConsLoString("world", mt)))
        && t.checkExpect(snoc3.normalize(), new ConsLoString("hey!", 
            new ConsLoString("Sean has cool hair", new ConsLoString("Reb is very smol", mt))))
        && t.checkExpect(append1.normalize(), mt)
        && t.checkExpect(append2.normalize(), cons1)
        && t.checkExpect(append3.normalize(), new ConsLoString("hey!", 
            new ConsLoString("Sean has cool hair", cons3)))
        && t.checkExpect(firstSnoc.normalize(), 
            new ConsLoString("w", new ConsLoString("x", mt)))
        && t.checkExpect(secondSnoc.normalize(), 
            new ConsLoString("y", new ConsLoString("z", mt)))
        && t.checkExpect(test.normalize(), new ConsLoString("w", new ConsLoString("x", 
            new ConsLoString("y", new ConsLoString("z", new ConsLoString("a", mt))))));
  }

  // test the method append() for MtLoString, ConsLoString, SnocLoString, and AppendLoString
  boolean testAppend(Tester t) {
    return t.checkExpect(snoc3.append(mt), snoc3.normalize())
        && t.checkExpect(snoc1.append(cons2), new ConsLoString("hey!", new ConsLoString("hola", 
            new ConsLoString("hey!", mt))))
        && t.checkExpect(cons1.append(cons2), new ConsLoString("hey!", new ConsLoString("hola", 
            new ConsLoString("hey!", mt))))
        && t.checkExpect(cons2.append(mt), cons2)
        && t.checkExpect(mt.append(mt), mt)
        && t.checkExpect(mt.append(append3), append3)
        && t.checkExpect(append4.append(mt), new AppendLoString(append4, mt))
        && t.checkExpect(append4.append(cons5), new AppendLoString(append4, cons5));
  }

  // test the method scanConcat() for MtLoString, ConsLoString, SnocLoString, and AppendLoString
  boolean testScanConcat(Tester t) {
    return t.checkExpect(cons4.scanConcat(), new ConsLoString("hello", 
        new ConsLoString("helloworld", mt)))
        && t.checkExpect(mt.scanConcat(), mt)
        && t.checkExpect(cons1.scanConcat(), cons1)
        && t.checkExpect(cons2.scanConcat(), new ConsLoString("hola", 
            new ConsLoString("holahey!", mt)))
        && t.checkExpect(append1.scanConcat(), mt)
        && t.checkExpect(append2.scanConcat(), cons1)
        && t.checkExpect(new SnocLoString(new SnocLoString(new SnocLoString(mt, "a"), "b"), 
            "c").scanConcat(), new ConsLoString("a", new ConsLoString("ab", 
                new ConsLoString("abc", mt))))
        && t.checkExpect(snoc4.scanConcat(), new ConsLoString("hey!", 
            new ConsLoString("hey!oh boy", mt)));
  }

  // test the method scanConcatHelper() for MtLoString, ConsLoString, SnocLoString, and 
  // AppendLoString
  boolean testScanConcatHelper(Tester t) {
    return t.checkExpect(mt.scanHelper("a"), mt)
        && t.checkExpect(cons4.scanHelper(""), new ConsLoString("hello", 
            new ConsLoString("helloworld", mt)))
        && t.checkExpect(mt.scanHelper(""), mt)
        && t.checkExpect(cons1.scanHelper(""), cons1)
        && t.checkExpect(cons2.scanHelper(""), new ConsLoString("hola", 
            new ConsLoString("holahey!", mt)))
        && t.checkExpect(append1.scanHelper(""), mt)
        && t.checkExpect(append2.scanHelper(""), cons1)
        && t.checkExpect(new SnocLoString(new SnocLoString(new SnocLoString(mt, "a"), "b"), 
            "c").scanHelper(""), new ConsLoString("a", new ConsLoString("ab", 
                new ConsLoString("abc", mt))))
        && t.checkExpect(snoc4.scanHelper(""), new ConsLoString("hey!", 
            new ConsLoString("hey!oh boy", mt)))
        && t.checkExpect(cons4.scanHelper("yoyoyo"), new ConsLoString("yoyoyohello", 
            new ConsLoString("yoyoyohelloworld", mt)));
  }
}
