package com.challenge.concurrency.collection;

public class Node<T extends Comparable<T>> {
    T value;
    Node left;
    Node right;
 
    public Node(T value) {
        this.value = value;
        right = null;
        left = null;
    }
}