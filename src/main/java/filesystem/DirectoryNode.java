package filesystem;

import java.util.*;

public class DirectoryNode implements FileSystemElement {
    private final String name;
    private final Map<String, FileSystemElement> children = new LinkedHashMap<>();
    private DirectoryNode parent;

    public DirectoryNode(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Directory name cannot be blank");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    @Override
    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    public void addElement(FileSystemElement element) {
        Objects.requireNonNull(element, "Element cannot be null");

        if (element == this) {
            throw new IllegalArgumentException("Cannot add directory to itself");
        }
        if (element.getParent() != null) {
            throw new IllegalStateException("Element already has a parent: " + element.getName());
        }
        if (children.containsKey(element.getName())) {
            throw new IllegalArgumentException("Duplicate sibling name detected: " + element.getName());
        }

        if (element instanceof DirectoryNode dirElement && isAncestorOf(dirElement, this)) {
            throw new IllegalArgumentException("Cycle detected: Ancestor cannot be added as child");
        }

        element.setParent(this);
        children.put(element.getName(), element);
    }

    private static boolean isAncestorOf(DirectoryNode potentialAncestor, DirectoryNode target) {
        DirectoryNode current = target;
        while (current != null) {
            if (current == potentialAncestor) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    public Collection<FileSystemElement> getChildren() {
        return Collections.unmodifiableCollection(children.values());
    }

    @Override
    public void accept(FileSystemVisitor visitor) {
        Objects.requireNonNull(visitor, "Visitor cannot be null");
        visitor.visit(this);
    }
}