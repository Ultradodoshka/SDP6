# Assignment 06

Hierarchical in-memory file system implementing the Visitor behavioral pattern and Double Dispatch (Java 17, Maven, JUnit 5).

## Prerequisites
- Java 17+
- Apache Maven 3.8+

## Execution Commands

### Run Automated Tests (T1–T8)
Executes all 8 checkpoint unit tests via the Surefire plugin:
```bash
mvn clean test
```

### Run Demonstration
Compiles and executes the deterministic sample tree run (`Main.java`):
```bash
mvn compile exec:java -Dexec.mainClass="filesystem.Main"
```

---

## Architectural Highlights

- **Double Dispatch:** Dispatched via `element.accept(visitor)` $\rightarrow$ `visitor.visit(this)`, resolving the concrete overload at compile time without `instanceof` or reflection.
- **Traversal Strategy (R7):** Traversal is handled by the visitors themselves, allowing search and statistics operations to maintain stateful path/depth stacks while keeping node elements clean.
- **Tree Integrity (R6):** Defends against negative sizes, cycle loops, duplicate sibling names, and multi-parent assignments.
- **Extensibility (R8):** `StatisticsVisitor` added without changing `FileNode`, `DirectoryNode`, or `accept()` signatures.

## Class Overview

```text
filesystem/
├── FileSystemElement (Interface: getName, getParent, setParent, accept)
│   ├── FileNode (Leaf: name, non-negative size)
│   └── DirectoryNode (Composite: unmodifiable children collection, cycle-safe)
│
├── FileSystemVisitor (Interface: visit(FileNode), visit(DirectoryNode))
│   ├── TotalSizeVisitor (R3: cumulative file size)
│   ├── SearchVisitor (R4: filter by name & min size, sorted root-relative paths)
│   └── StatisticsVisitor (R5: file count, dir count, max depth where root=0)
│
└── Main (Executable demonstration)
```