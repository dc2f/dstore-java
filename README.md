dstore
======

distributed, fully versioned, optionally typed tree store written in java.

Think of it as git for any tree-structured data.

Stage: Prototype


Architecture
-------

com.dc2f.dstore.storage: Flat storage where everything is broken down to be stored with UIDs, (no direct references)
com.dc2f.dstore.hierarchynodestore: Based upon the storage builds a hierarchical tree of nodes.
