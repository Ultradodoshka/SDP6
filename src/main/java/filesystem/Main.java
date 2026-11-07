package filesystem;

public class Main {
    public static void main(String[] args) {
        // Демонстрационная иерархия с именованием depthX
        DirectoryNode depth0 = new DirectoryNode("depth0");
        DirectoryNode depth1A = new DirectoryNode("depth1A");
        DirectoryNode depth1B = new DirectoryNode("depth1B");
        DirectoryNode depth2 = new DirectoryNode("depth2");

        depth0.addElement(depth1A);
        depth0.addElement(depth1B);
        depth1A.addElement(depth2);

        depth0.addElement(new FileNode("root_config.json", 250));
        depth1A.addElement(new FileNode("data_large.csv", 4096));
        depth2.addElement(new FileNode("nested_item.txt", 1024));

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        depth0.accept(sizeVisitor);
        System.out.println("Total size: " + sizeVisitor.getTotalSize() + " bytes");

        SearchVisitor searchVisitor = new SearchVisitor("data", 1000);
        depth0.accept(searchVisitor);
        System.out.println("Search results: " + searchVisitor.getMatchingPaths());

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        depth0.accept(statsVisitor);
        System.out.println("Files: "+statsVisitor.getFileCount()+" Dirs: "+statsVisitor.getDirectoryCount()+", Max Depth: "+statsVisitor.getMaxDepth());
    }
}