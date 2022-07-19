import java.util.ArrayList;
import java.util.List;

public class Edge {
    private int id;
    private int startVertexId;
    private int finishvertexId;
    private double weight;
    private double length;

    public List<Integer> getAllNodesInEdge() {
        return allNodesInEdge;
    }

    private List<Integer> allNodesInEdge = new ArrayList<>();
    private int maxSpeed;
    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getStartVertexId() {
        return startVertexId;
    }

    public void setStartVertId(int firstVert) {
        this.startVertexId = firstVert;
    }

    public int getFinishvertexId() {
        return finishvertexId;
    }
    public void setFinishVertId(int secondVert) {
        this.finishvertexId = secondVert;
    }

    public Edge(int id, int firstVert, int secondVert) {
        setId(id);
        setStartVertId(firstVert);
        setFinishVertId(secondVert);
    }
    public Edge(int id) {
        setId(id);
    }

    public int getOtherNode(int id){
        if(id==startVertexId){
            return finishvertexId;
        } else {
            return startVertexId;
        }
    }

    public List<Integer> getEdgeNodes(long vertexIndex){
        if(allNodesInEdge.indexOf(vertexIndex)==0){
            return allNodesInEdge;
        } else{
            List<Integer> nodeIds = new ArrayList<>();
            for(int i = allNodesInEdge.size()-1; i > -1; i--){
                nodeIds.add(allNodesInEdge.get(i));
            }
            return nodeIds;
        }
    }
}
