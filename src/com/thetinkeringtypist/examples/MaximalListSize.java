package com.thetinkeringtypist.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Let's test a simple case of allocating and filling a list based on the amount of memory available and not based on
 * some arbitrary number. Useful for most people? Probably not. BUT! This is useful when most of your items are stored
 * in memory while waiting to be processed. Say a queue of some type.
 * 
 * The better solution here might be to limit the size of the collection based on sound estimation and engineering.
 * Heck, you might even be better off writing long waiting tasks to disk or some sort of long term persistent memory.
 *
 * But we can't always do that. So... let's say that we have a microservice that runs in duplicate/triplicate/etc.
 * on different hardware across the deployment space (i.e. heterogeneous hardware). How do we know how many objects a
 * collection can hold if all the hardware is different (i.e. different amount of RAM available to each microservice)?
 *
 * To that, you should read the second "paragraph" again. Then again, when we're messing around at home for fun,
 * why not just play around and find out?
 */
public class MaximalListSize {
    public static void main(String[] args) {
        // Print out some basic memory info before starting
        System.gc(); // Manual GC invocation. Attempt to get approximate memory measurements.
        final long freeBefore = Runtime.getRuntime().freeMemory() / 1024 / 1024;    // MiB

        try {
            System.out.printf("[BEFORE] Free Heap Space:  %s MiB%n", freeBefore);
            System.out.println();

            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(-1);
        }

        // The meat of the test:
        //
        // First, let's try to allocate a list of size a given size (Integer.MAX_VALUE).
        //
        // If that works, let's try to populate it. There's no point to allocating a list of a
        // given capacity unless we can fill it, right?
        //
        // If that works, great! We're all done. But... what if that should fail? What do we do
        // if we can allocate a list of a large size, but we can't fill it?
        //
        // Simple, we try a smaller number! But how do we choose that number? We could decrement by 1,
        // but that could take an awfully long time to test if we're working with a limited amount of
        // memory. Why not try cutting that previous number in half instead? It's certainly faster.
        //
        // But how do we try again? We can catch Errors just as well as Exceptions. When we do that,
        // clear out the list if it was allocated and set it to null to allow the GC to reclaim that
        // space for us. Otherwise, that memory won't be available to use again and our test ends prematurely.
        //
        // Once we reach a capacity that can be both allocated and filled, that's what we'll leave it at.
        // It doesn't make a lot of sense to then search again in the positive direction. Truthfully, once
        // we find this "maximal" number, it's probably best to work with a still smaller number to make
        // sure our program still has some heap space to work with. Otherwise, this exercise doesn't lead
        // to anything practical.
        //
        // Finally, print some memory measurements for fun.

        int capacity = Integer.MAX_VALUE;
        List<Object> list = null;

        while (capacity > 0) {
            try {
                list = new ArrayList<>(capacity);

                // The objects added to the list here isn't anything special.
                // This can change to a more complex object for this test to have more meaning.
                //
                // NOTE: We don't want to add any items that exceed the allocated capacity.
                // ArrayList will attempt to double the available capacity under the hood,
                // potentially exceeding the amount of memory available to the JVM.
                for (int i = 0; i < capacity; i++) {
                    list.add(new Object());
                }

                break;
            } catch (OutOfMemoryError e) {
                // If we don't clear out this list, we can't continue with the test
                if (Objects.nonNull(list)) {
                    list.clear();
                    list = null;
                }

                System.out.printf("Can't allocate %,d objects with a list of the same size.%n", capacity);
                capacity >>= 1; // Fancy divide by 2, because why not?
                System.out.printf("Retrying allocation with capacity %,d%n", capacity);
                System.out.println();
            }
        }

        // Print memory usage comparison after a successful test
        System.gc(); // Manual GC invocation. Attempt to get approximate memory measurements.
        final long freeAfter = Runtime.getRuntime().freeMemory() / 1024 / 1024;    // MiB

        System.out.println();
        System.out.printf("[BEFORE] Free Heap Space:  %s MiB%n", freeBefore);
        System.out.printf("[AFTER]  Free Heap Space:  %s MiB%n", freeAfter);
        System.out.println();

        // By the time we get here, the list will never be null. However, the
        // compiler can't guarantee that. So we'll add this check to make it happy.
        if (Objects.nonNull(list)) {
            System.out.printf("Allocated and fully populated list with %,d objects%n", list.size());
        }
    }
}
