# StaticBucketQueue
A java implementation of a bucket queue supporting min and max operations. The Queue only requeres 2 heap allocations, one to store the items and one for metadata. the queue needs to know the max number of elements per bucket and number of buckets up front.
