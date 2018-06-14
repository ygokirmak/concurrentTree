package com.challenge.concurrency.collection;

import javafx.util.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentBinaryTree<T extends  Comparable<T>> implements Collection {

    protected Node<T> root = null;
    protected volatile int size = 0;

    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    private boolean addToTree(Node current, T value) {


        // start with root node
        Node curr = root;

        // pointer to store parent node of current node
        Node parent = null;

        // if tree is empty, create a new node and set root
        if (root == null) {
            root = new Node<T>(value);
            return true;
        }

        // traverse the tree and find parent node of key
        while (curr != null)
        {
            // update parent node as current node
            parent = curr;

            // if given key is less than the current node,
            // go to left subtree else go to right subtree
            if (value.compareTo((T) curr.value) == -1) {
                curr = curr.left;
            }
            else {
                curr = curr.right;
            }
        }

        // construct a new node and assign to appropriate parent pointer
        if (value.compareTo((T) parent.value) == -1) {
            parent.left = new Node<T>(value);
        }
        else if (value.compareTo((T) parent.value) == 1)  {
            parent.right = new Node<T>(value);
        }else{
            return false;
        }

        return true;
    }

    @Override
    public boolean add(Object o) {

        lock.writeLock().lock();

        boolean isAdded = addToTree(root, (T) o);

        if( isAdded ){
            size++;
        }

        lock.writeLock().unlock();

        return isAdded;
    }


    @Override
    public int size() {
        lock.readLock().lock();
        int treeSize = this.size;
        lock.readLock().unlock();

        return treeSize;

    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public Iterator iterator() {
        throw new NotImplementedException();
    }

    @Override
    public Object[] toArray() {
        throw new NotImplementedException();
    }

    @Override
    public boolean remove(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(Collection c) {
        throw new NotImplementedException();
    }

    @Override
    public void clear() {
        throw new NotImplementedException();
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new NotImplementedException();
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new NotImplementedException();
    }
}
