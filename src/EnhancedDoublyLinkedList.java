import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;

/**
 * Enhanced Doubly Linked List with additional algorithms and optimizations
 * Features:
 * - O(1) insertion and deletion at both ends
 * - O(n/2) average case for get() using bidirectional search
 * - Merge sort implementation for O(n log n) sorting
 * - Binary search for sorted lists
 * - Iterator with bidirectional traversal
 * - Enhanced error handling and validation
 */
public class EnhancedDoublyLinkedList<T> implements Iterable<T> {
    private DoublyLinkedNode<T> head;
    private DoublyLinkedNode<T> tail;
    private int size;
    private boolean isSorted = false;
    private Comparator<T> lastUsedComparator = null;

    public EnhancedDoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Add element to the end of the list - O(1)
     */
    public void add(T data) {
        addLast(data);
    }

    /**
     * Add element to the end of the list - O(1)
     */
    public void addLast(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        
        DoublyLinkedNode<T> newNode = new DoublyLinkedNode<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        size++;
        isSorted = false; // List is no longer guaranteed to be sorted
    }

    /**
     * Add element to the beginning of the list - O(1)
     */
    public void addFirst(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        
        DoublyLinkedNode<T> newNode = new DoublyLinkedNode<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            head.setPrev(newNode);
            newNode.setNext(head);
            head = newNode;
        }
        size++;
        isSorted = false;
    }

    /**
     * Insert element at specific index - O(n/2) average case
     */
    public void add(int index, T data) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (data == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }

        if (index == 0) {
            addFirst(data);
            return;
        }
        if (index == size) {
            addLast(data);
            return;
        }

        DoublyLinkedNode<T> newNode = new DoublyLinkedNode<>(data);
        DoublyLinkedNode<T> current = getNodeAt(index);
        
        newNode.setNext(current);
        newNode.setPrev(current.getPrev());
        current.getPrev().setNext(newNode);
        current.setPrev(newNode);
        
        size++;
        isSorted = false;
    }

    /**
     * Remove first element - O(1)
     */
    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        
        T data = head.getData();
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = head.getNext();
            head.setPrev(null);
        }
        size--;
        return data;
    }

    /**
     * Remove last element - O(1)
     */
    public T removeLast() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        
        T data = tail.getData();
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            tail = tail.getPrev();
            tail.setNext(null);
        }
        size--;
        return data;
    }

    /**
     * Remove element at specific index - O(n/2) average case
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == 0) {
            return removeFirst();
        }
        if (index == size - 1) {
            return removeLast();
        }

        DoublyLinkedNode<T> current = getNodeAt(index);
        T data = current.getData();
        
        current.getPrev().setNext(current.getNext());
        current.getNext().setPrev(current.getPrev());
        
        size--;
        return data;
    }

    /**
     * Get element at index with optimized bidirectional search - O(n/2) average case
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        return getNodeAt(index).getData();
    }

    /**
     * Set element at index - O(n/2) average case
     */
    public void set(int index, T data) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (data == null) {
            throw new IllegalArgumentException("Cannot set null element");
        }
        
        getNodeAt(index).setData(data);
        isSorted = false;
    }

    /**
     * Optimized node retrieval using bidirectional search
     * Searches from head if index < size/2, otherwise from tail
     */
    private DoublyLinkedNode<T> getNodeAt(int index) {
        DoublyLinkedNode<T> current;
        
        if (index < size / 2) {
            // Search from head
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
        } else {
            // Search from tail
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.getPrev();
            }
        }
        
        return current;
    }

    /**
     * Find index of first occurrence of element - O(n)
     */
    public int indexOf(T data) {
        if (data == null) return -1;
        
        DoublyLinkedNode<T> current = head;
        for (int i = 0; i < size; i++) {
            if (data.equals(current.getData())) {
                return i;
            }
            current = current.getNext();
        }
        return -1;
    }

    /**
     * Find index of last occurrence of element - O(n)
     */
    public int lastIndexOf(T data) {
        if (data == null) return -1;
        
        DoublyLinkedNode<T> current = tail;
        for (int i = size - 1; i >= 0; i--) {
            if (data.equals(current.getData())) {
                return i;
            }
            current = current.getPrev();
        }
        return -1;
    }

    /**
     * Check if list contains element - O(n)
     */
    public boolean contains(T data) {
        return indexOf(data) != -1;
    }

    /**
     * Sort the list using merge sort - O(n log n)
     */
    public void sort(Comparator<T> comparator) {
        if (size <= 1) {
            isSorted = true;
            lastUsedComparator = comparator;
            return;
        }
        
        head = mergeSort(head, comparator);
        
        // Update tail reference
        DoublyLinkedNode<T> current = head;
        while (current.getNext() != null) {
            current = current.getNext();
        }
        tail = current;
        
        isSorted = true;
        lastUsedComparator = comparator;
    }

    /**
     * Recursive merge sort implementation
     */
    private DoublyLinkedNode<T> mergeSort(DoublyLinkedNode<T> node, Comparator<T> comparator) {
        if (node == null || node.getNext() == null) {
            return node;
        }

        // Split the list into two halves
        DoublyLinkedNode<T> middle = getMiddle(node);
        DoublyLinkedNode<T> nextOfMiddle = middle.getNext();
        middle.setNext(null);
        nextOfMiddle.setPrev(null);

        // Recursively sort both halves
        DoublyLinkedNode<T> left = mergeSort(node, comparator);
        DoublyLinkedNode<T> right = mergeSort(nextOfMiddle, comparator);

        // Merge the sorted halves
        return merge(left, right, comparator);
    }

    /**
     * Find middle node for merge sort
     */
    private DoublyLinkedNode<T> getMiddle(DoublyLinkedNode<T> node) {
        if (node == null) return node;

        DoublyLinkedNode<T> slow = node;
        DoublyLinkedNode<T> fast = node.getNext();

        while (fast != null && fast.getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
        }

        return slow;
    }

    /**
     * Merge two sorted lists
     */
    private DoublyLinkedNode<T> merge(DoublyLinkedNode<T> left, DoublyLinkedNode<T> right, Comparator<T> comparator) {
        DoublyLinkedNode<T> dummy = new DoublyLinkedNode<>(null);
        DoublyLinkedNode<T> current = dummy;

        while (left != null && right != null) {
            if (comparator.compare(left.getData(), right.getData()) <= 0) {
                current.setNext(left);
                left.setPrev(current);
                left = left.getNext();
            } else {
                current.setNext(right);
                right.setPrev(current);
                right = right.getNext();
            }
            current = current.getNext();
        }

        // Attach remaining nodes
        if (left != null) {
            current.setNext(left);
            left.setPrev(current);
        } else if (right != null) {
            current.setNext(right);
            right.setPrev(current);
        }

        DoublyLinkedNode<T> result = dummy.getNext();
        if (result != null) {
            result.setPrev(null);
        }
        return result;
    }

    /**
     * Binary search for sorted list - O(log n)
     * Only works if list is sorted with the same comparator
     */
    public int binarySearch(T key, Comparator<T> comparator) {
        if (!isSorted || !comparator.equals(lastUsedComparator)) {
            throw new IllegalStateException("List must be sorted with the same comparator before binary search");
        }
        
        return binarySearchRecursive(0, size - 1, key, comparator);
    }

    /**
     * Recursive binary search implementation
     */
    private int binarySearchRecursive(int left, int right, T key, Comparator<T> comparator) {
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;
        T midValue = get(mid);
        int comparison = comparator.compare(key, midValue);

        if (comparison == 0) {
            return mid;
        } else if (comparison < 0) {
            return binarySearchRecursive(left, mid - 1, key, comparator);
        } else {
            return binarySearchRecursive(mid + 1, right, key, comparator);
        }
    }

    /**
     * Reverse the list - O(n)
     */
    public void reverse() {
        if (size <= 1) return;

        DoublyLinkedNode<T> current = head;
        DoublyLinkedNode<T> temp = null;

        // Swap next and prev pointers for all nodes
        while (current != null) {
            temp = current.getPrev();
            current.setPrev(current.getNext());
            current.setNext(temp);
            current = current.getPrev(); // Move to next node (which was previous due to swap)
        }

        // Swap head and tail
        temp = head;
        head = tail;
        tail = temp;
        
        isSorted = false;
    }

    /**
     * Get list size - O(1)
     */
    public int getSize() {
        return size;
    }

    /**
     * Check if list is empty - O(1)
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Get first element - O(1)
     */
    public T getFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        return head.getData();
    }

    /**
     * Get last element - O(1)
     */
    public T getLast() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.getData();
    }

    /**
     * Clear the list - O(1)
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
        isSorted = false;
        lastUsedComparator = null;
    }

    /**
     * Convert to array - O(n)
     */
    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        if (array.length < size) {
            array = (T[]) java.lang.reflect.Array.newInstance(
                array.getClass().getComponentType(), size);
        }

        DoublyLinkedNode<T> current = head;
        for (int i = 0; i < size; i++) {
            array[i] = current.getData();
            current = current.getNext();
        }

        if (array.length > size) {
            array[size] = null;
        }

        return array;
    }

    /**
     * Check if list is sorted
     */
    public boolean isSorted() {
        return isSorted;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        DoublyLinkedNode<T> current = head;
        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) {
                sb.append(" <-> ");
            }
            current = current.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new ForwardIterator();
    }

    /**
     * Get reverse iterator
     */
    public Iterator<T> reverseIterator() {
        return new ReverseIterator();
    }

    /**
     * Forward iterator implementation
     */
    private class ForwardIterator implements Iterator<T> {
        private DoublyLinkedNode<T> current = head;
        private DoublyLinkedNode<T> lastReturned = null;
        private int expectedSize = size;

        @Override
        public boolean hasNext() {
            checkForModification();
            return current != null;
        }

        @Override
        public T next() {
            checkForModification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturned = current;
            current = current.getNext();
            return lastReturned.getData();
        }

        @Override
        public void remove() {
            checkForModification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            
            EnhancedDoublyLinkedList.this.remove(indexOf(lastReturned.getData()));
            expectedSize--;
            lastReturned = null;
        }

        private void checkForModification() {
            if (expectedSize != size) {
                throw new java.util.ConcurrentModificationException();
            }
        }
    }

    /**
     * Reverse iterator implementation
     */
    private class ReverseIterator implements Iterator<T> {
        private DoublyLinkedNode<T> current = tail;
        private DoublyLinkedNode<T> lastReturned = null;
        private int expectedSize = size;

        @Override
        public boolean hasNext() {
            checkForModification();
            return current != null;
        }

        @Override
        public T next() {
            checkForModification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturned = current;
            current = current.getPrev();
            return lastReturned.getData();
        }

        @Override
        public void remove() {
            checkForModification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            
            EnhancedDoublyLinkedList.this.remove(lastIndexOf(lastReturned.getData()));
            expectedSize--;
            lastReturned = null;
        }

        private void checkForModification() {
            if (expectedSize != size) {
                throw new java.util.ConcurrentModificationException();
            }
        }
    }
}