import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PathContainer {

    private Map<Integer, VisitedVertex> visitedpathVertexMap;

    public Map<Integer, VisitedVertex> getVisitedpathVertexMap() {
        return visitedpathVertexMap;
    }
    public PathContainer(){
        visitedpathVertexMap = new HashMap<>();
    }

    public void addOrChangeVertexPath(VisitedVertex visitedVertex){
        if(visitedpathVertexMap.containsKey(visitedVertex.getId())){
            visitedpathVertexMap.get(visitedVertex.getId()).setPrevVertexId(visitedVertex.getPrevVertexId());
            visitedpathVertexMap.get(visitedVertex.getId()).setEdgeWeightsFromStart(visitedVertex.getEdgeWeightsFromStart());
            visitedpathVertexMap.get(visitedVertex.getId()).setDistWeightFromStart(visitedVertex.getDistWeightFromStart());
        }else{
            visitedpathVertexMap.put(visitedVertex.getId(), visitedVertex);
        }
    }
    public List<GraphNode> getVertexPath(int finishId, Graph graph){
        List<GraphNode> path = new ArrayList<>();
       //path.add(graph.getVertexMap().get(finishId));
        getPrevVertexes(finishId,path, graph);
        return path;
    }
    private void getPrevVertexes(int vertexid, List<GraphNode> path, Graph graph) {
        int prevId = visitedpathVertexMap.get(vertexid).getPrevVertexId();
        if ( prevId != 0) {
            List<GraphNode> nodeIds = graph.getAllNodesInEdge(vertexid,prevId);
            path.addAll(nodeIds);
            getPrevVertexes(prevId, path , graph);
        }
    }

    public void printPath(List<GraphNode> vertexes){
        vertexes.forEach(e->{
            System.out.println(e.toString() + ", " );
        });
        System.out.println("LINESTRING(" + vertexes.stream().map(GraphNode::getWKTCoordinates).collect(Collectors.joining(",")) + ")");
    }
}
