# Some Java Data Structures
This repository contains several common data structures, made using Java, that I made and used during my enrollment in Northeastern's [CS 2510 Accelerated](https://course.ccs.neu.edu/cs2510a) course. They were created using guidance from the homeworks, labs, lectures, and exams created by my professor, [Benjamin Lerner](https://www.khoury.northeastern.edu/people/benjamin-lerner/). 

## A breakdown of the different files
| File | Description |
| ----------- | ----------- |
| BinarySearchTree.java | A binary search tree that can have elements inserted, find data at a node with a specified search function, find its size, get data at a specified index, and can be iterated over using Java's built-in loops. |
| Deque.java | A circular backwards and fowards linked list that can add elements to the front and end, remove elements from the front and end, remove a specified node, find its size, find nodes in the structure matching a specified predicate, and can be iterated over using Java's built-in loops. |
| Lists.java | A non-mutable list that's either empty or non-empty. |
| OrderableMultiSet.java | An ordered multiset (an ordered list with possibly multiple of the same element) built with a binary search tree as the core structure. |
| StacksAndQueues.java | A "worklist" data structure which is either a stack or a queue. |


## What is tester.jar?
I use Ben Lerner's tester library (contained in tester.jar) to test my code! To use it, include it in whatever project contains these .java files as an external jar and set your run configurations to use tester.Main as the main class, with the name of the Examples___ class as the program argument. 
