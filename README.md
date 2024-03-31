# Java Resource Limitation Examples

This repository contains some examples of how to tackle certain problems when dealing with resource limits when using
Java. Unless specified otherwise, all examples (when executed) should have the JVM arguments `-Xms` and `-Xmx` set to
the same value.

## Finding "Maximal" Size of a List
File: [com.thetinkeringtypist.examples.MaximalListSize.java](src/com/thetinkeringtypist/examples/MaximalListSize.java)

This is a short test exploring dynamic memory allocation limits with java collections. I was interested in exploring
how large a list could be allocated and filled. In a distributed environment where the same component might be running
in duplicate/triplicate/etc. on different hardware (complete with different memory limits), I was interested to see if
collections could be allocated dynamically without crashing based on arbitrary numbers.

A proper solution to this problem is to do some actual design and engineering work to come up with a plan for dealing
with a large collection of objects. However, for those more interested in how many objects they can actually store in
memory during execution, this applies.
