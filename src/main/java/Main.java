import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        OsmParser osmParser = new OsmParser();
        Document document = osmParser.getDocument();

        Graph graph = osmParser.graph;
        System.out.println(System.currentTimeMillis());
        //сначала собираем читаем нужные дороги и собираем точки содаем объекты и полностью их заполняем
        Node node = document.getFirstChild();
        NodeList nodeList = node.getChildNodes();
        osmParser.CheckWays(nodeList);
        osmParser.getEdgesAndNodes(nodeList);
        System.out.println(graph.CountCrossRoad());
        CoordinatesTree root = new CoordinatesTree(nodeList,4);
        graph.fillVertexAndEdgesMap();
        graph.getVertexesFromEdges(root);
        graph.setEdgeWeights(args[4]);

        GraphNode start = new GraphNode( Double.parseDouble(args[0]), Double.parseDouble(args[1]));
        GraphNode finish = new GraphNode( Double.parseDouble(args[2]), Double.parseDouble(args[3]));

        Set<Integer> nearestVertexes = root.getNearestVertexes(start);
        Set<Integer> nearestVertexes1 = root.getNearestVertexes(finish);
        if(nearestVertexes.size()==0 || nearestVertexes1.size()==0){
            System.out.println("Точка вне рассматриваемой зоны или произошла ошибка");
        }

        HashMap<Integer,GraphNode> nearStartvertex = new HashMap<>();
        nearestVertexes.forEach(e->{
            nearStartvertex.put(e,graph.getVertexMap().get(e));
        });
        HashMap<Integer,GraphNode> nearFinishtvertex = new HashMap<>();
        nearestVertexes1.forEach(e->{
            nearFinishtvertex.put(e,graph.getVertexMap().get(e));
        });

        GraphNode startVertex  = graph.getClosestVertex(nearStartvertex,start);
        GraphNode finVertex  = graph.getClosestVertex(nearFinishtvertex,finish);

        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm();

        PathContainer pathContainer = dijkstraAlgorithm.CheckVertexes(graph,startVertex,finVertex);
        List<GraphNode> path = pathContainer.getVertexPath(finVertex.getId(), graph);
        pathContainer.printPath(path);

        System.out.println(pathContainer.getVisitedpathVertexMap().get(finVertex.getId()).getEdgeWeightsFromStart());

    }
}
