
public class StaticBucketQueue {

    // entire data structure only has 2 heap allocations
    // store all entries in one continuous array in memory
    private final int[] buckets;
    // store the starting point of each bucket i at offsetsAndCounts[2i] and the count of bucket[i] at offsetsAndCounts[2i+1] and a sentinal value at the end
    private final int[] offsetsAndCounts;

    // HINTS: These track the range of populated buckets to avoid scanning empty ones.
    private int minPriority = 0;
    private int maxPriority = -1;

    private int size = 0;


    /**
     * Constructor: client supplies an array of the maximum sizes of the buckets
     *
     * @param bucketSizes creates a StaticBucketQueue that can except items with priority from 0 to bucketSizes.length - 1.
     * The maximum number of items of priority i that can be stored at any one time is bucketSizes[i]
     *
     */
    public StaticBucketQueue(int[] bucketSizes) {
        int numBuckets = bucketSizes.length;
        this.offsetsAndCounts = new int[2 * numBuckets + 1];

        int currentOffset = 0;
        for (int i = 0; i < numBuckets; i++) {
            offsetsAndCounts[2 * i] = currentOffset;
            currentOffset += bucketSizes[i];
        }
        offsetsAndCounts[2 * numBuckets] = currentOffset;
        this.buckets = new int[currentOffset];

    }


    /**
     * Constructor: client supplies the total number of buckets, and the number of items that can be stored per bucket
     *
     * @param numBuckets creates a StaticBucketQueue that can except items with priority from 0 to numBuckets - 1.
     *
     * @param bucketSize can store at most bucketSize items of any given priority at one time
     */
    public StaticBucketQueue(int numBuckets, int bucketSize) {
        this.offsetsAndCounts = new int[2 * numBuckets + 1];

        int currentOffset = 0;
        for (int i = 0; i <= numBuckets; i++) {
            offsetsAndCounts[2*i] = currentOffset;
            currentOffset += bucketSize;
        }
        offsetsAndCounts[2 * numBuckets] = currentOffset;
        this.buckets = new int[currentOffset];
    }

    /**
//     * Insert: O(1)
     *
     * @param id the id of the element to be inserted.
     *
     * @param priority the priority of the element being inserted
     *
     * @throws IllegalArgumentException if the client gives an invalid priority
     *
     * @throws IllegalStateException if the client give a priority
     */
    public void insert(int id, int priority) {
        if (priority < 0 || priority >= offsetsAndCounts.length/2) { // if priority is invalid
            throw new IllegalArgumentException("Invalid priority");
        }

        // Capacity Check
        int index = offsetsAndCounts[priority * 2] + offsetsAndCounts[priority * 2 + 1];
        if (index >= offsetsAndCounts[priority * 2 + 2]) { // if bucket is full
            throw new IllegalStateException("Bucket " + priority + " is full.");
        }

        // insert items in Lifo order to simplify keeping track of where to insert and remove elements from
        buckets[index] = id; // insert
        offsetsAndCounts[2 * priority + 1]++; // update counts
        size++;

        // Update Hints
        // If we inserted a priority lower than our current min, update min.
        if (priority < minPriority) {
            minPriority = priority;
        }
        // If we inserted a priority higher than our current max, update max.
        if (priority > maxPriority) {
            maxPriority = priority;
        }


    }

    /**
     *
     * @return the amount of elements in the bucketHeap
     */
    public int size() {
        return size;
    }

    /**
     *
     * @return true if contains 0 elements
     */
    public boolean isEmpty() {
        return size == 0;
    }

    // ================ Min Operations =================


    /**
     * sets the min hint to point to the minimum non-empty bucket
     * worst case: O(numBuckets) usually O(1) because of hints
     * @return the non-empty bucket of minimum priority
     *
     * @throws IllegalArgumentException if the queue is empty
     */
    public int getMinPriority() {
        ensureNotEmpty();
        // Adjust hint forward if necessary
        while (minPriority < offsetsAndCounts.length / 2 && offsetsAndCounts[minPriority * 2 + 1] == 0) {
            minPriority++;
        }
        return minPriority;
    }

    /**
     * return element of minimum priority in the bucketQueue
     * worst case: O(numBuckets) usually O(1) because of hints
     * @return the element of minimum priority int the heap
     * @throws IllegalArgumentException if the bucketQueue is empty
     */
    public int peekMin() {
        int p = getMinPriority(); // This validates and adjusts minActivePriority
        return buckets[offsetsAndCounts[p * 2] + offsetsAndCounts[p * 2 + 1]];
    }

    /**
     * pop element of minimum priority in the bucketQueue
     * worst case: O(numBuckets) usually O(1) because of hints
     * @return the element of minimum priority int the heap
     * @throws IllegalArgumentException if the bucketQueue is empty
     */
    public int popMin() {
        int p = getMinPriority();

        int element = buckets[offsetsAndCounts[p * 2] + offsetsAndCounts[p * 2 + 1]];
        offsetsAndCounts[2 * p + 1]--;
        size--;
        // we will lazily update the min hint when we next call getMinPriority

        return element;
    }

    // ================ Max Operations =================


    /**
     * sets the max hint to point to the maximum non-empty bucket
     * worst case: O(numBuckets) usually O(1) because of hints
     * @return the non-empty bucket of maximum priority
     *
     * @throws IllegalArgumentException if the queue is empty
     */
    public int getMaxPriority() {
        ensureNotEmpty();
        // Adjust hint backward if necessary
        while (maxPriority >= 0 && offsetsAndCounts[maxPriority * 2 + 1] == 0) {
            maxPriority--;
        }
        return maxPriority;
    }

    /**
     * return element of maximum priority in the bucketQueue
     * worst case: O(numBuckets) usually O(1) because of hints
     * @return the element of maximum priority in the queue
     * @throws IllegalArgumentException if the bucketQueue is empty
     */
    public int peekMax() {
        int p = getMaxPriority(); // This validates and adjusts minActivePriority
        return buckets[offsetsAndCounts[p * 2] + offsetsAndCounts[p * 2 + 1]];
    }

    /**
     * pop element of maximum priority in the bucketQueue
     * worst case: O(numBuckets) usually O(1) because of hints
     * @return the element of maximum priority in the queue
     * @throws IllegalArgumentException if the bucketQueue is empty
     */
    public int popMax() {
        int p = getMaxPriority();
        int element = buckets[offsetsAndCounts[p * 2] + offsetsAndCounts[p * 2 + 1]];
        offsetsAndCounts[2 * p + 1]--;
        size--;
        // we will lazily update the max hint when we next call getMinPriority
        return element;
    }

    // ============== Utilities ==========


    private void ensureNotEmpty() {
        if (size == 0) throw new IllegalStateException("Queue is empty");
    }



}
