/* ListSorts.java */

import list.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ListSorts {

    private final static int SORTSIZE = 1000000;

    /**
     * makeQueueOfQueues() makes a queue of queues, each containing one item
     * of q.  Upon completion of this method, q is empty.
     *
     * @param q is a LinkedQueue of objects.
     * @return a LinkedQueue containing LinkedQueue objects, each of which
     * contains one object from q.
     **/
    public static LinkedQueue makeQueueOfQueues(LinkedQueue q) {
        // Replace the following line with your solution.
        LinkedQueue q2 = new LinkedQueue();
        while (!q.isEmpty()) {
            LinkedQueue q1 = new LinkedQueue();
            try {
                q1.enqueue(q.dequeue());
                q2.enqueue(q1);
            } catch (QueueEmptyException e) {
                System.out.println("never happens");
            }
        }
        return q2;
    }

    /**
     * mergeSortedQueues() merges two sorted queues into a third.  On completion
     * of this method, q1 and q2 are empty, and their items have been merged
     * into the returned queue.
     *
     * @param q1 is LinkedQueue of Comparable objects, sorted from smallest
     *           to largest.
     * @param q2 is LinkedQueue of Comparable objects, sorted from smallest
     *           to largest.
     * @return a LinkedQueue containing all the Comparable objects from q1
     * and q2 (and nothing else), sorted from smallest to largest.
     **/
    public static LinkedQueue mergeSortedQueues(LinkedQueue q1, LinkedQueue q2) {
        // Replace the following line with your solution.
        LinkedQueue q3 = new LinkedQueue();
        while (!q1.isEmpty() && !q2.isEmpty()) {
            try {
                Comparable obj1 = (Comparable) q1.front();
                Comparable obj2 = (Comparable) q2.front();
                if (obj1.compareTo(obj2) < 0) {
                    q3.enqueue(q1.dequeue());
                } else {
                    q3.enqueue(q2.dequeue());
                }
            } catch (QueueEmptyException e) {
                System.out.println("never happens");
            }
        }
        if (q1.isEmpty()) {
            q3.append(q2);
        } else {
            q3.append(q1);
        }
        return q3;
    }

    /**
     * partition() partitions qIn using the pivot item.  On completion of
     * this method, qIn is empty, and its items have been moved to qSmall,
     * qEquals, and qLarge, according to their relationship to the pivot.
     *
     * @param qIn     is a LinkedQueue of Comparable objects.
     * @param pivot   is a Comparable item used for partitioning.
     * @param qSmall  is a LinkedQueue, in which all items less than pivot
     *                will be enqueued.
     * @param qEquals is a LinkedQueue, in which all items equal to the pivot
     *                will be enqueued.
     * @param qLarge  is a LinkedQueue, in which all items greater than pivot
     *                will be enqueued.
     **/
    public static void partition(LinkedQueue qIn, Comparable pivot,
                                 LinkedQueue qSmall, LinkedQueue qEquals,
                                 LinkedQueue qLarge) {
        // Your solution here.
        while (!qIn.isEmpty()) {
            try {
                Comparable obj = (Comparable) qIn.dequeue();
                if (obj.compareTo(pivot) < 0) {
                    qSmall.enqueue(obj);
                } else if (obj.compareTo(pivot) > 0 ) {
                    qLarge.enqueue(obj);
                } else {
                    qEquals.enqueue(obj);
                }
            } catch (QueueEmptyException e) {
                System.out.println("supposed to be never happened");
            }
        }
    }


    /**
     * mergeSort() sorts q from smallest to largest using mergesort.
     *
     * @param q is a LinkedQueue of Comparable objects.
     **/
    public static void mergeSort(LinkedQueue q) {
        // Your solution here.
        LinkedQueue qoq = makeQueueOfQueues(q);
        try {
            while (qoq.size() > 1) {
                LinkedQueue q1 = (LinkedQueue) qoq.dequeue();
                LinkedQueue q2 = (LinkedQueue) qoq.dequeue();
                qoq.enqueue(mergeSortedQueues(q1, q2));
            }
            LinkedQueue sortedQ = (LinkedQueue) qoq.dequeue();
            q.append(sortedQ);
        } catch (QueueEmptyException e) {
            System.out.println("never happens");
        }
    }

    /**
     * quickSort() sorts q from smallest to largest using quicksort.
     *
     * @param q is a LinkedQueue of Comparable objects.
     **/
    public static void quickSort(LinkedQueue q) {
        // Your solution here.
        if (!q.isEmpty()) {
            int n = new Random().nextInt(q.size()) + 1;
            Comparable pivot = (Comparable) q.nth(n);
            LinkedQueue small = new LinkedQueue();
            LinkedQueue eql = new LinkedQueue();
            LinkedQueue lar = new LinkedQueue();
            partition(q, pivot, small, eql, lar);
            quickSort(small);
            quickSort(lar);
            q.append(small);
            q.append(eql);
            q.append(lar);
        }
    }

    /**
     * makeRandom() builds a LinkedQueue of the indicated size containing
     * Integer items.  The items are randomly chosen between 0 and size - 1.
     *
     * @param size is the size of the resulting LinkedQueue.
     **/
    public static LinkedQueue makeRandom(int size) {
        LinkedQueue q = new LinkedQueue();
        for (int i = 0; i < size; i++) {
            q.enqueue(new Integer((int) (size * Math.random())));
        }
        return q;
    }


    /**
     * Bucket Sort
     */

    public static ArrayList bucketSort(ArrayList<ArrayList> records, HashMap<String, Integer> sortingSequence, int factorIdx) {
        int numberOfBuckets = sortingSequence.size();
        ArrayList[] bucketOfBuckets = new ArrayList[numberOfBuckets];
        for (ArrayList record: records) {
            int bucketIdx = (Integer) record.get(factorIdx);
            bucketOfBuckets[bucketIdx].add(record);
        }
        ArrayList re = new ArrayList();
        for (int i=0; i<numberOfBuckets; i++) {
            re.addAll(bucketOfBuckets[i]);
        }
        return re;
    }

    /**
     * main() performs some tests on mergesort and quicksort.  Feel free to add
     * more tests of your own to make sure your algorithms works on boundary
     * cases.  Your test code will not be graded.
     **/
    public static void main(String[] args) {

        LinkedQueue q = makeRandom(10);
        System.out.println(q.toString());
        mergeSort(q);
        System.out.println(q.toString());

        q = makeRandom(10);
        System.out.println(q.toString());
        quickSort(q);
        System.out.println(q.toString());

    /* Remove these comments for Part III.*/
        Timer stopWatch = new Timer();
        q = makeRandom(SORTSIZE);
        stopWatch.start();
        mergeSort(q);
        stopWatch.stop();
        System.out.println("Mergesort time, " + SORTSIZE + " Integers:  " +
                           stopWatch.elapsed() + " msec.");

        stopWatch.reset();
        q = makeRandom(SORTSIZE);
        stopWatch.start();
        quickSort(q);
        stopWatch.stop();
        System.out.println("Quicksort time, " + SORTSIZE + " Integers:  " +
                           stopWatch.elapsed() + " msec.");
    }

}
