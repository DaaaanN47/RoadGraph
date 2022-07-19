import java.util.*;

public class Graph {
    Set<Long> allNodesIds = new HashSet<>();

    double distance;
    //хранит в себе все перектерски(вершины)
    private Map<Integer, GraphNode> vertexMap = new HashMap<>();

    public Map<Integer, List<Integer>> getVertexesAndItsEdges() {
        return vertexesAndItsEdges;
    }

    //хранит в себе айдишки вершин и список прилегающих ребер
    private Map<Integer, List<Integer>> vertexesAndItsEdges = new HashMap<>();
    Map<Integer,Edge> edgeMap = new HashMap<>();

    public Map<Integer, OSMNode> getNodeMap() {
        return nodeMap;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Map<Integer, GraphNode> getVertexMap() {
        return vertexMap;
    }

    public Map<Integer, Edge> getEdgeMap() {
        return edgeMap;
    }
    int nodeIdsCounter = 0;
    int edgeIdsCounter = 0;

    int graphNodeId = 0;
    int count;
    private java.util.Map<Integer, OSMNode> nodeMap = new HashMap<>();
    private Set<Edge> edges = new HashSet<>();

    public Set<Long> getAllNodesIds() {
        return allNodesIds;
    }
    public void addNewNode(long id, OsmParser osmParser){
        if(!allNodesIds.contains(id)){
            allNodesIds.add(id);
            osmParser.getLongIntegerIds().put(id,nodeIdsCounter);
            osmParser.getIntegerLongIds().put(nodeIdsCounter,id);
            OSMNode osmNode = new OSMNode(osmParser.longIntegerIds.get(id));
            nodeMap.put(osmNode.getId(),osmNode);
            nodeIdsCounter++;
        } else {
            int intId = osmParser.longIntegerIds.get(id);
            nodeMap.get(intId).setIsCrossRoad();
        }
    }
    public Edge addNewEdge(){
        Edge edge = new Edge(edgeIdsCounter);
        edgeIdsCounter++;
        return edge;
    }
    public int CountCrossRoad(){
        nodeMap.entrySet().forEach(e->{
            if (e.getValue().isCrossRoad()){
                count++;
            }
        });
        return count;
    }

    public void fillVertexAndEdgesMap(){
        edges.forEach(e-> {
            List<Integer> list = vertexesAndItsEdges.getOrDefault(e.getStartVertexId(),new ArrayList<>());
            list.add(e.getId());
            vertexesAndItsEdges.put(e.getStartVertexId(),list);
            list = vertexesAndItsEdges.getOrDefault(e.getFinishvertexId(),new ArrayList<>());
            list.add(e.getId());
            vertexesAndItsEdges.put(e.getFinishvertexId(),list);
        });
    }
    public void getVertexesFromEdges(CoordinatesTree root){
        vertexesAndItsEdges.forEach((key, value) -> {
            GraphNode graphNode = new GraphNode(key, nodeMap.get(key).getLat(), nodeMap.get(key).getLon());
            vertexMap.put(graphNode.getId(), graphNode);
            root.addVertex(graphNode);
        });
    }
    public void setEdgeWeights(String weightType){
        if(weightType.equals("Расстояние")){
            edges.forEach(e->{
                calculateEdgeLength(e);
                e.setWeight(e.getLength());
            });
        } else {
            edges.forEach(e->{
                calculateEdgeLength(e);
                e.setWeight((e.getLength()/1000)/e.getMaxSpeed());
            });
        }
    }
    private void calculateEdgeLength(Edge edge) {
        double length=0;
        OSMNode node = nodeMap.get(edge.getStartVertexId());
        for(int i = 1; i<edge.getAllNodesInEdge().size(); i++) {
            length= length + CalculateDistance(node.getLat(), node.getLon(), nodeMap.get(edge.getAllNodesInEdge().get(i)).getLat(),nodeMap.get(edge.getAllNodesInEdge().get(i)).getLon());
            node = nodeMap.get(edge.getAllNodesInEdge().get(i));
        }
        edge.setLength(length);
    }
    private double CalculateDistance(double strLat, double strLon, double finLat, double finLon){
        double radius = 6378137;
        double degreeConvert = Math.PI/180;

        double x1  = Math.cos(degreeConvert * strLat) * Math.cos(degreeConvert * strLon);
        double x2  = Math.cos(degreeConvert * finLat) * Math.cos(degreeConvert * finLon);

        double y1  = Math.cos(degreeConvert * strLat) * Math.sin(degreeConvert * strLon);
        double y2  = Math.cos(degreeConvert * finLat) * Math.sin(degreeConvert * finLon);

        double z1  = Math.sin(degreeConvert * strLat);
        double z2  = Math.sin(degreeConvert * finLat);

        return radius * Math.acos(x1 * x2 + y1 * y2 + z1 * z2);
    }
    public List<GraphNode> getAllNodesInEdge(int startId, int finishId){
        List<GraphNode> vertices = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getOtherNode(startId) == finishId && edge.getOtherNode(finishId) == startId) {
                List<Integer> nodesIds =  edge.getEdgeNodes(startId);
                nodesIds.forEach(id ->{
                    double lat = nodeMap.get(id).getLat();
                    double lon = nodeMap.get(id).getLon();
                    GraphNode vertex = new GraphNode(id,lat,lon);
                    vertices.add(vertex);
                });
                return vertices;
            }
        }
        return null;
    }
    public GraphNode getClosestVertex(HashMap<Integer,GraphNode> vertexMap, GraphNode vertex){
        distance = Double.MAX_VALUE;
        vertexMap.forEach((key, value) -> {
            double newDist = CalculateDistance(vertex.getLat(), vertex.getLon(), value.getLat(), value.getLon());
            if (distance > newDist ) {
                distance = newDist;
                graphNodeId = value.getId();
            }
        });
        return vertexMap.get(graphNodeId);
    }
}
