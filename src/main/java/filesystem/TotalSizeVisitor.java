package filesystem;

public class TotalSizeVisitor implements FileSystemVisitor {
    private long totalSize = 0;

    public void reset() {this.totalSize = 0;}

    public long getTotalSize() {return totalSize;}

    @Override
    public void visit(FileNode file) {
        totalSize += file.getSize();
    }

    @Override
    public void visit(DirectoryNode directory) {
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }
    }
}