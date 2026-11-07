package filesystem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisitorFileSystemTest {

    @Test
    @DisplayName("T1: Both element types reach the correct overloaded visit methods")
    void testDoubleDispatchResolution() {
        class SpyVisitor implements FileSystemVisitor {
            boolean fileHit = false;
            boolean dirHit = false;

            @Override
            public void visit(FileNode file) {
                fileHit = true;
            }

            @Override
            public void visit(DirectoryNode directory) {
                dirHit = true;
            }
        }

        SpyVisitor spy = new SpyVisitor();
        FileNode file = new FileNode("file.txt", 100);
        DirectoryNode dir = new DirectoryNode("depth0");

        file.accept(spy);
        assertTrue(spy.fileHit);
        assertFalse(spy.dirHit);

        dir.accept(spy);
        assertTrue(spy.dirHit);
    }

    @Test
    @DisplayName("T2: Size accumulation across multiple nesting levels is correct")
    void testTotalSizeAccumulation() {
        DirectoryNode depth0 = new DirectoryNode("depth0");
        DirectoryNode depth1 = new DirectoryNode("depth1");
        DirectoryNode depth2 = new DirectoryNode("depth2");

        depth0.addElement(depth1);
        depth1.addElement(depth2);

        depth0.addElement(new FileNode("f0.bin", 500));
        depth1.addElement(new FileNode("f1.bin", 1500));
        depth2.addElement(new FileNode("f2.bin", 2000));

        TotalSizeVisitor visitor = new TotalSizeVisitor();
        depth0.accept(visitor);

        assertEquals(4000, visitor.getTotalSize());
    }

    @Test
    @DisplayName("T3: Search correctly combines both configurable filter conditions")
    void testSearchWithMultipleCriteria() {
        DirectoryNode depth0 = new DirectoryNode("depth0");
        depth0.addElement(new FileNode("log_match.txt", 1500));
        depth0.addElement(new FileNode("log_small.txt", 300));
        depth0.addElement(new FileNode("data_large.csv", 5000));

        SearchVisitor visitor = new SearchVisitor("log", 1000);
        depth0.accept(visitor);

        List<String> results = visitor.getMatchingPaths();
        assertEquals(1, results.size());
        assertEquals("depth0/log_match.txt", results.get(0));
    }

    @Test
    @DisplayName("T4: Search produces correct, deterministic, root-relative paths sorted lexicographically")
    void testSearchDeterministicRootRelativePaths() {
        DirectoryNode depth0 = new DirectoryNode("depth0");
        DirectoryNode depth1 = new DirectoryNode("depth1");
        DirectoryNode depth2 = new DirectoryNode("depth2");

        depth0.addElement(depth1);
        depth1.addElement(depth2);

        depth2.addElement(new FileNode("z_file.log", 100));
        depth2.addElement(new FileNode("a_file.log", 100));
        depth1.addElement(new FileNode("m_file.log", 100));

        SearchVisitor visitor = new SearchVisitor(".log", 0);
        depth0.accept(visitor);

        List<String> results = visitor.getMatchingPaths();
        List<String> expected = List.of(
                "depth0/depth1/depth2/a_file.log",
                "depth0/depth1/depth2/z_file.log",
                "depth0/depth1/m_file.log"
        );
        assertEquals(expected, results);
    }

    @Test
    @DisplayName("T5: Statistics are correct for nesting, counting and maximum depth")
    void testStatisticsCollection() {
        // depth0 is root (depth 0)
        DirectoryNode depth0 = new DirectoryNode("depth0");
        DirectoryNode depth1 = new DirectoryNode("depth1");
        DirectoryNode depth2 = new DirectoryNode("depth2");
        FileNode leaf = new FileNode("leaf.txt", 50);

        depth0.addElement(depth1);
        depth1.addElement(depth2);
        depth2.addElement(leaf);

        StatisticsVisitor visitor = new StatisticsVisitor();
        depth0.accept(visitor);

        assertEquals(1, visitor.getFileCount());
        assertEquals(3, visitor.getDirectoryCount());
        assertEquals(3, visitor.getMaxDepth());
    }

    @Test
    @DisplayName("T6: Empty structures and no-match search cases are handled")
    void testEmptyAndNoMatch() {
        DirectoryNode emptyRoot = new DirectoryNode("depth0");

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        emptyRoot.accept(sizeVisitor);
        assertEquals(0, sizeVisitor.getTotalSize());

        SearchVisitor searchVisitor = new SearchVisitor("missing", 0);
        emptyRoot.accept(searchVisitor);
        assertTrue(searchVisitor.getMatchingPaths().isEmpty());

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        emptyRoot.accept(statsVisitor);
        assertEquals(0, statsVisitor.getFileCount());
        assertEquals(1, statsVisitor.getDirectoryCount());
        assertEquals(0, statsVisitor.getMaxDepth());
    }

    @Test
    @DisplayName("T7: Repeat executions do not leak or multiply accumulated state")
    void testRepeatedExecutionStateIntegrity() {
        DirectoryNode depth0 = new DirectoryNode("depth0");
        depth0.addElement(new FileNode("sample.txt", 800));

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        depth0.accept(sizeVisitor);
        assertEquals(800, sizeVisitor.getTotalSize());

        sizeVisitor.reset();
        depth0.accept(sizeVisitor);
        assertEquals(800, sizeVisitor.getTotalSize());
    }

    @Test
    @DisplayName("T8: Invalid tree mutations are rejected while valid nesting remains supported")
    void testTreeInvariantsAndRejection() {
        DirectoryNode depth0 = new DirectoryNode("depth0");
        FileNode file = new FileNode("valid.txt", 200);
        depth0.addElement(file);

        assertThrows(IllegalArgumentException.class, () -> new FileNode("invalid.txt", -10));

        assertThrows(IllegalArgumentException.class, () -> depth0.addElement(new FileNode("valid.txt", 300)));

        DirectoryNode depth1 = new DirectoryNode("depth1");
        assertThrows(IllegalStateException.class, () -> depth1.addElement(file));

        assertThrows(IllegalArgumentException.class, () -> depth0.addElement(depth0));

        depth0.addElement(depth1);
        assertThrows(IllegalArgumentException.class, () -> depth1.addElement(depth0));
    }
}