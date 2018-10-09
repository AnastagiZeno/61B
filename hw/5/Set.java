/* Set.java */

import list.*;

/**
 * A Set is a collection of Comparable elements stored in sorted order.
 * Duplicate elements are not permitted in a Set.
 **/
public class Set {
    /* Fill in the data fields here. */
    DList list;

    /**
     * Set ADT invariants:
     *  1)  The Set's elements must be precisely the elements of the List.
     *  2)  The List must always contain Comparable elements, and those elements
     *      must always be sorted in ascending order.
     *  3)  No two elements in the List may be equal according to compareTo().
     **/

    /**
     * Constructs an empty Set.
     * <p>
     * Performance:  runs in O(1) time.
     **/
    public Set() {
        // Your solution here.
        list = new DList();

    }

    /**
     * cardinality() returns the number of elements in this Set.
     * <p>
     * Performance:  runs in O(1) time.
     **/
    public int cardinality() {
        // Replace the following line with your solution.
        return list.length();
    }

    /**
     * insert() inserts a Comparable element into this Set.
     * <p>
     * Sets are maintained in sorted order.  The ordering is specified by the
     * compareTo() method of the java.lang.Comparable interface.
     * <p>
     * Performance:  runs in O(this.cardinality()) time.
     **/
    public void insert(Comparable c) {
        // Your solution here.
        try {
            ListNode n;
            for (n = list.front(); n.isValidNode(); n = n.next()) {
                if (c.compareTo(n.item()) < 0) {
                    n.insertBefore(c);
                    return;
                } else if (c.compareTo(n.item()) == 0) {
                    return;
                }
            }
            list.insertBack(c);
        } catch (InvalidNodeException e) {
            System.out.println("********************************");
        }
    }

    /**
     * union() modifies this Set so that it contains all the elements it
     * started with, plus all the elements of s.  The Set s is NOT modified.
     * Make sure that duplicate elements are not created.
     * <p>
     * Performance:  Must run in O(this.cardinality() + s.cardinality()) time.
     * <p>
     * Your implementation should NOT copy elements of s or "this", though it
     * will copy _references_ to the elements of s.  Your implementation will
     * create new _nodes_ for the elements of s that are added to "this", but
     * you should reuse the nodes that are already part of "this".
     * <p>
     * DO NOT MODIFY THE SET s.
     * DO NOT ATTEMPT TO COPY ELEMENTS; just copy _references_ to them.
     **/
    public void union(Set s) {
        // Your solution here.
        ListNode oNode = list.front();
        ListNode sNode = s.list.front();
        try {
            while (oNode.isValidNode() && sNode.isValidNode()) {
                int oCompare2s = ((Comparable)oNode.item()).compareTo( ((Comparable)sNode.item()));
                if ( oCompare2s < 0) {
                    oNode = oNode.next();
                } else if (oCompare2s == 0) {
                    sNode = sNode.next();
                } else {
                    oNode.insertBefore(sNode.item());
                    sNode = sNode.next();
                }
            }

            if (!oNode.isValidNode()) {
                while (sNode.isValidNode()) {
                    list.insertBack(sNode.item());
                    sNode = sNode.next();
                }
            }

        } catch (InvalidNodeException e) {
            System.out.println("********************************");
        }
    }

    /**
     * intersect() modifies this Set so that it contains the intersection of
     * its own elements and the elements of s.  The Set s is NOT modified.
     * <p>
     * Performance:  Must run in O(this.cardinality() + s.cardinality()) time.
     * <p>
     * Do not construct any new ListNodes during the execution of intersect.
     * Reuse the nodes of "this" that will be in the intersection.
     * <p>
     * DO NOT MODIFY THE SET s.
     * DO NOT CONSTRUCT ANY NEW NODES.
     * DO NOT ATTEMPT TO COPY ELEMENTS.
     **/
    public void intersect(Set s) {
        // Your solution here.
        ListNode oNode = list.front();
        ListNode sNode = s.list.front();
        ListNode temp;
        try {
            while (sNode.isValidNode() && oNode.isValidNode()) {
                int oCompare2s = ((Comparable)oNode.item()).compareTo( ((Comparable)sNode.item()));
                if (oCompare2s < 0) {
                    temp = oNode;
                    oNode = oNode.next();
                    temp.remove();
                } else if (oCompare2s == 0) {
                    sNode = sNode.next();
                    oNode = oNode.next();
                } else {
                    sNode = sNode.next();
                }
            }

            if (oNode.isValidNode()) {
                while (oNode.isValidNode()) {
                    temp = oNode;
                    oNode = oNode.next();
                    temp.remove();
                }
            }
        } catch (InvalidNodeException e) {
            System.out.println("********************************");
        }
    }


    /**
     * toString() returns a String representation of this Set.  The String must
     * have the following format:
     * {  } for an empty Set.  No spaces before "{" or after "}"; two spaces
     * between them.
     * {  1  2  3  } for a Set of three Integer elements.  No spaces before
     * "{" or after "}"; two spaces before and after each element.
     * Elements are printed with their own toString method, whatever
     * that may be.  The elements must appear in sorted order, from
     * lowest to highest according to the compareTo() method.
     * <p>
     * WARNING:  THE AUTOGRADER EXPECTS YOU TO PRINT SETS IN _EXACTLY_ THIS
     * FORMAT, RIGHT UP TO THE TWO SPACES BETWEEN ELEMENTS.  ANY
     * DEVIATIONS WILL LOSE POINTS.
     **/
    public String toString() {
        // Replace the following line with your solution.
        String s = "[ ";
        ListNode n;
        try {
            for (n = list.front(); n.isValidNode(); n = n.next()) {
                s = s + n.item() + " ";
            }
        } catch (InvalidNodeException e) {
            return "?";
        }
        return s + "]";
    }

    public static void main(String[] argv) {
        Set s = new Set();
        s.insert(new Integer(3));
        s.insert(new Integer(4));
        s.insert(new Integer(3));
        System.out.println("Set s = " + s);

        System.out.println("s.cardinality() = " + s.cardinality());

        Set s2 = new Set();
        s2.insert(new Integer(4));
        s2.insert(new Integer(5));
        s2.insert(new Integer(5));
        System.out.println("Set s2 = " + s2);

        Set s3 = new Set();
        s3.insert(new Integer(5));
        s3.insert(new Integer(3));
        s3.insert(new Integer(8));
        System.out.println("Set s3 = " + s3);

        s.union(s2);
        System.out.println("After s.union(s2), s = " + s);

        System.out.println("s.cardinality() = " + s.cardinality());

        s.intersect(s3);
        System.out.println("After s.intersect(s3), s = " + s);

        System.out.println("s.cardinality() = " + s.cardinality());
        // You may want to add more (ungraded) test code here.
    }
}
