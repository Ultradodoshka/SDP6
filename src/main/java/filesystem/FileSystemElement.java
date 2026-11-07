package filesystem;

public interface FileSystemElement {
    String getName();
    DirectoryNode getParent();
    void setParent(DirectoryNode parent);
    void accept(FileSystemVisitor visitor);
}
