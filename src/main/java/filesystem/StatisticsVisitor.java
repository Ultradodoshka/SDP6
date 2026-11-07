package filesystem;

public class StatisticsVisitor implements FileSystemVisitor {
    private int fileCount =0;
    private int directoryCount= 0;
    private int maxDepth= 0;
    private int currentDepth= 0;

    public void reset() {
        this.fileCount= 0;
        this.directoryCount= 0;
        this.maxDepth= 0;
        this.currentDepth= 0;
    }

    public int getFileCount() {return fileCount;}

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }

        currentDepth++;
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }
        currentDepth--;
    }
}