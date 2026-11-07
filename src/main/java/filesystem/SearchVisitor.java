package filesystem;

import java.util.*;

public class SearchVisitor implements FileSystemVisitor {
    private final String nameCriterion;
    private final long minSizeInclusive;
    private final List<String> matches = new ArrayList<>();
    private final Deque<String> pathSegments = new ArrayDeque<>();

    public SearchVisitor(String nameCriterion, long minSizeInclusive) {
        this.nameCriterion = (nameCriterion == null) ? "" : nameCriterion;
        if (minSizeInclusive < 0) {
            throw new IllegalArgumentException("Min size can not be negative");
        }
        this.minSizeInclusive = minSizeInclusive;
    }

    public void reset() {
        this.matches.clear();
        this.pathSegments.clear();
    }

    public List<String> getMatchingPaths() {
        List<String> sorted = new ArrayList<>(matches);
        Collections.sort(sorted);
        return Collections.unmodifiableList(sorted);
    }

    private String buildPath(String fileName) {
        if (pathSegments.isEmpty()) {
            return fileName;
        }
        return String.join("/", pathSegments) + "/" + fileName;
    }

    @Override
    public void visit(FileNode file) {
        if (file.getName().contains(nameCriterion) && file.getSize() >= minSizeInclusive) {
            matches.add(buildPath(file.getName()));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        pathSegments.addLast(directory.getName());
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }
        pathSegments.removeLast();
    }
}