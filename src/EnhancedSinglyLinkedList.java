import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;

/**
 * Enhanced Singly Linked List with improved algorithms and functionality
 * Features:
 * - O(1) insertion at head
 * - O(n) insertion at tail with tail pointer optimization
 * - Merge sort for O(n log n) sorting
 * - Fast cycle detection using Floyd's algorithm
 * - Enhanced error handling and validation
 * - Iterator support with fail-fast behavior
 * 
 * AUTHOR: Enhanced version based on John Carlo Palipa's implementation
 * SUBJECT: DATA STRUCTURE IT212 9458
 */
public class EnhancedSinglyLinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail; // Optimization for O(1) tail insertion
    private int size;
    private boolean isSorted = false;
    private Comparator<T> lastUsedComparator = null;

    public EnhancedSinglyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Add element to the end of the list - O(1) with tail pointer
     */
    public void add(T data) {
        addLast(data);
    }

    /**
     * Add element to the end of the list - O(1) with tail pointer
     */
    public void addLast(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
        isSorted = false;
    }

    /**
     * Add element to the beginning of the list - O(1)
     */
    public void addFirst(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head = newNode;
        }
        size++;
        isSorted = false;
    }

    /**
     * Insert element at specific index - O(n)
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

        Node<T> newNode = new Node<>(data);
        Node<T> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.getNext();
        }
        
        newNode.setNext(current.getNext());
        current.setNext(newNode);
        size++;
        isSorted = false;
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
     * Get last element - O(1) with tail pointer
     */
    public T getLast() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.getData();
    }

    /**
     * Get element at index - O(n)
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current.getData();
    }

    /**
     * Set element at index - O(n)
     */
    public void set(int index, T data) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (data == null) {
            throw new IllegalArgumentException("Cannot set null element");
        }
        
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        current.setData(data);
        isSorted = false;
    }

    /**
     * Remove element at index - O(n)
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == 0) {
            return removeFirst();
        }

        Node<T> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.getNext();
        }
        
        T data = current.getNext().getData();
        Node<T> nodeToRemove = current.getNext();
        current.setNext(nodeToRemove.getNext());
        
        // Update tail if we removed the last element
        if (nodeToRemove == tail) {
            tail = current;
        }
        
        size--;
        return data;
    }

    /**
     * Remove first element - O(1)
     */
    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        
        T data = head.getData();
        head = head.getNext();
        
        if (head == null) {
            tail = null;
        }
        
        size--;
        return data;
    }

    /**
     * Remove last element - O(n) due to singly linked nature
     */
    public T removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        if (size == 1) {
            return removeFirst();
        }
        
        // Find second to last node
        Node<T> current = head;
        while (current.getNext() != tail) {
            current = current.getNext();
        }
        
        T data = tail.getData();
        current.setNext(null);
        tail = current;
        size--;
        return data;
    }

    /**
     * Find index of first occurrence - O(n)
     */
    public int indexOf(T data) {
        if (data == null) return -1;
        
        Node<T> current = head;
        for (int i = 0; i < size; i++) {
            if (data.equals(current.getData())) {
                return i;
            }
            current = current.getNext();
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
        Node<T> current = head;
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
    private Node<T> mergeSort(Node<T> node, Comparator<T> comparator) {
        if (node == null || node.getNext() == null) {
            return node;
        }

        // Split the list into two halves using fast/slow pointer technique
        Node<T> middle = getMiddle(node);
        Node<T> nextOfMiddle = middle.getNext();
        middle.setNext(null);

        // Recursively sort both halves
        Node<T> left = mergeSort(node, comparator);
        Node<T> right = mergeSort(nextOfMiddle, comparator);

        // Merge the sorted halves
        return merge(left, right, comparator);
    }

    /**
     * Find middle node using fast/slow pointer technique
     */
    private Node<T> getMiddle(Node<T> node) {
        if (node == null) return node;

        Node<T> slow = node;
        Node<T> fast = node.getNext();

        while (fast != null && fast.getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
        }

        return slow;
    }

    /**
     * Merge two sorted lists
     */
    private Node<T> merge(Node<T> left, Node<T> right, Comparator<T> comparator) {
        Node<T> dummy = new Node<>(null);
        Node<T> current = dummy;

        while (left != null && right != null) {
            if (comparator.compare(left.getData(), right.getData()) <= 0) {
                current.setNext(left);
                left = left.getNext();
            } else {
                current.setNext(right);
                right = right.getNext();
            }
            current = current.getNext();
        }

        // Attach remaining nodes
        current.setNext(left != null ? left : right);

        return dummy.getNext();
    }

    /**
     * Detect cycle in the list using Floyd's cycle detection algorithm - O(n)
     */
    public boolean hasCycle() {
        if (head == null || head.getNext() == null) {
            return false;
        }

        Node<T> slow = head;
        Node<T> fast = head;

        while (fast != null && fast.getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
            
            if (slow == fast) {
                return true;
            }
        }

        return false;
    }

    /**
     * Find the starting node of a cycle (if exists) - O(n)
     */
    public Node<T> findCycleStart() {
        if (!hasCycle()) {
            return null;
        }

        Node<T> slow = head;
        Node<T> fast = head;

        // Find meeting point
        while (fast != null && fast.getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
            if (slow == fast) {
                break;
            }
        }

        // Move slow to head and keep fast at meeting point
        slow = head;
        while (slow != fast) {
            slow = slow.getNext();
            fast = fast.getNext();
        }

        return slow; // Start of cycle
    }

    /**
     * Reverse the list iteratively - O(n)
     */
    public void reverse() {
        if (size <= 1) return;

        Node<T> prev = null;
        Node<T> current = head;
        tail = head; // Old head becomes new tail
        
        while (current != null) {
            Node<T> next = current.getNext();
            current.setNext(prev);
            prev = current;
            current = next;
        }
        
        head = prev; // New head
        isSorted = false;
    }

    /**
     * Get the middle element of the list - O(n)
     */
    public T getMiddleElement() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }

        Node<T> slow = head;
        Node<T> fast = head;

        while (fast != null && fast.getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
        }

        return slow.getData();
    }

    /**
     * Remove duplicates from sorted list - O(n)
     * Only works if the list is sorted
     */
    public void removeDuplicatesFromSorted() {
        if (head == null) return;

        Node<T> current = head;
        while (current.getNext() != null) {
            if (current.getData().equals(current.getNext().getData())) {
                Node<T> nodeToDelete = current.getNext();
                current.setNext(nodeToDelete.getNext());
                
                if (nodeToDelete == tail) {
                    tail = current;
                }
                size--;
            } else {
                current = current.getNext();
            }
        }
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

        Node<T> current = head;
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
        Node<T> current = head;
        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) {
                sb.append(" -> ");
            }
            current = current.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new SinglyLinkedListIterator();
    }

    /**
     * Iterator implementation with fail-fast behavior
     */
    private class SinglyLinkedListIterator implements Iterator<T> {
        private Node<T> current = head;
        private Node<T> lastReturned = null;
        private Node<T> beforeLast = null;
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
            
            beforeLast = lastReturned;
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
            
            if (beforeLast == null) {
                // Removing first element
                head = lastReturned.getNext();
                if (head == null) {
                    tail = null;
                }
            } else {
                beforeLast.setNext(lastReturned.getNext());
                if (lastReturned == tail) {
                    tail = beforeLast;
                }
            }
            
            size--;
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