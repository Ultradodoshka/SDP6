package filesystem;

public interface FileSystemVisitor {
    void visit(FileNode file);
    void visit(DirectoryNode directory);
}