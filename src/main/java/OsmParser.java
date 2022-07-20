import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;

public class OsmParser {

    private static final String TAG_HIGHWAY = "highway";
    public Graph graph;

    Map<String, Integer> roadAndSpeed;
    Map<Long,String> ways = new HashMap<>();
    Map<Long, Integer> longIntegerIds = new HashMap<>();
    Map<Integer,Long> integerLongIds = new HashMap<>();

    public Map<Long, Integer> getLongIntegerIds() {
        return longIntegerIds;
    }

    public Map<Integer, Long> getIntegerLongIds() {
        return integerLongIds;
    }


    private static final Set<String> roadTypes = Stream.of("trunk","motorway", "primary", "secondary",
            "tertiary", "unclassified", "residential", "motorway_link", "trunk_link", "primary_link",
            "secondary_link", "tertiary_link", "living_street", "service", "track", "road").collect(Collectors.toSet());

    private void fillRoadSpeedMap() throws IOException {
        //E:\MaksimProject\src\main\resources\config.properties
        ///home/kochnev_a/projects/untitled/src/main/resources/config.properties
        //E:\рефактор проекта\RoadGraph\src\main\resources\config.properties
        FileInputStream fileInputStream = new FileInputStream("E:\\Refactor project\\RoadGraph\\src\\main\\resources\\config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        roadAndSpeed = new HashMap<>();
        roadTypes.forEach(roadType->{
            int speed = Integer.parseInt(properties.getProperty(roadType));
            roadAndSpeed.put(roadType, speed);
        });
    }
    public Document getDocument() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        graph = new Graph();
        fillRoadSpeedMap();
        String dir = System.getProperty("user.dir") + "\\src\\NAB-CH.osm";
        ///home/kochnev_a/projects/untitled/src/NAB-CH.osm
        return builder.parse("E:\\Refactor project\\RoadGraph\\src\\main\\NAB-CH.osm");
    }

    public void CheckWays(NodeList nodeList) throws IOException {

        int nodeListLength = nodeList.getLength();
        for (int i = 0; i < nodeListLength; i++) {
            if(!nodeList.item(i).getNodeName().equals("way")){
                continue;
            } else if (nodeList.item(i).getNodeName().equals("relation")) {
                break;
            }
            CheckWayParams(nodeList.item(i));
        }
    }
    private void CheckWayParams(Node way){
        NamedNodeMap attributes = way.getAttributes();
        NodeList tagList = way.getChildNodes();
        int tagListLength = tagList.getLength()-1;
        for (int j=tagListLength; j>0; j--) {
            Node refNode = tagList.item(j);
            if (tagList.item(j).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            NamedNodeMap refAttributes = refNode.getAttributes();
            if (refNode.getNodeName().equals("tag")) {
                String keyStr = refAttributes.getNamedItem("k").getNodeValue();
                if (keyStr.equals(TAG_HIGHWAY)) {
                    String valStr = refAttributes.getNamedItem("v").getNodeValue();
                    boolean isAdded = roadTypes.add(valStr);
                    if(!isAdded){
                        GetNodes(attributes, tagList, valStr);
                    }
                    else{
                        roadTypes.remove(valStr);
                    }
                    break;
                }
            }
        }
    }
    private void GetNodes(NamedNodeMap atributes, NodeList tagList, String roadType){
        ways.put(parseLong(atributes.getNamedItem("id").getNodeValue()), roadType);
        int tagListLength = tagList.getLength();
        for(int i =0 ; i<tagListLength; i++) {
            Node refNode = tagList.item(i);
            NamedNodeMap refAttributes = refNode.getAttributes();
            if (tagList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if(refNode.getNodeName().equals("nd")){
                long ref = Long.parseLong(refAttributes.getNamedItem("ref").getNodeValue());
                graph.addNewNode(ref, this);
            }
            else{
                break;
            }
        }
    }
    public void getEdgesAndNodes(NodeList nodeList){
        int nodeListLength = nodeList.getLength();
        for (int i = 0; i < nodeListLength; i++) {
            Node node = nodeList.item(i);
            if(node.getNodeName().equals("node")){
                NamedNodeMap attributes = node.getAttributes();
                long nodeId = Long.parseLong(attributes.getNamedItem("id").getNodeValue());
                boolean isContain = graph.getAllNodesIds().contains(nodeId);
                if (isContain) {
                    OSMNode osmNode = graph.getNodeMap().get(longIntegerIds.get(nodeId));
                    osmNode.setLat(Double.parseDouble(attributes.getNamedItem("lat").getNodeValue()));
                    osmNode.setLon(Double.parseDouble(attributes.getNamedItem("lon").getNodeValue()));
                }
            }
            if(node.getNodeName().equals("way")){
                NamedNodeMap attributes = node.getAttributes();
                long wayId = Long.parseLong(attributes.getNamedItem("id").getNodeValue());
                getEdgesFromWayNode(node,ways.get(wayId));
            }
        }
    }
    public void getEdgesFromWayNode(Node way, String wayType){
        NamedNodeMap attributes = way.getAttributes();
        long wayId = Long.parseLong(attributes.getNamedItem("id").getNodeValue());
        int startId = 0;
        long nodeId = 0;
        if(ways.containsKey(wayId)){
            NodeList tagList = way.getChildNodes();
            int tagListLength = tagList.getLength();
            Edge edge = new Edge(0);
            for(int i = 0; i<tagListLength; i++){
                Node refNode = tagList.item(i);
                if (refNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                if(refNode.getNodeName().equals("nd")){
                    NamedNodeMap refAttributes = refNode.getAttributes();
                    nodeId = Long.parseLong(refAttributes.getNamedItem("ref").getNodeValue());
                    if(startId == 0){
                        edge =  graph.addNewEdge();
                        startId = longIntegerIds.get(nodeId);
                        edge.setStartVertId(startId);
                        graph.getNodeMap().get(startId).setIsCrossRoad();
                        edge.getAllNodesInEdge().add(startId);
                    } else {
                        if(graph.getNodeMap().get(longIntegerIds.get(nodeId)).isCrossRoad()){
                            edge.setStartVertId(startId);
                            graph.getNodeMap().get(startId).setIsCrossRoad();

                            edge.setFinishVertId(longIntegerIds.get(nodeId));
                            graph.getNodeMap().get(longIntegerIds.get(nodeId)).setIsCrossRoad();

                            edge.getAllNodesInEdge().add(longIntegerIds.get(nodeId));
                            edge.setMaxSpeed(roadAndSpeed.get(wayType));
                            graph.getEdgeMap().put(edge.getId(),edge);
                            graph.getEdges().add(edge);
                            edge =  graph.addNewEdge();
                            startId = longIntegerIds.get(nodeId);
                            edge.setStartVertId(startId);
                            graph.getNodeMap().get(startId).setIsCrossRoad();
                            edge.getAllNodesInEdge().add(startId);
                        } else {
                            edge.getAllNodesInEdge().add(longIntegerIds.get(nodeId));
                        }
                    }
                }
                if(!refNode.getNodeName().equals("nd")) {
                    edge.setFinishVertId(longIntegerIds.get(nodeId));
                    graph.getNodeMap().get(longIntegerIds.get(nodeId)).setIsCrossRoad();
                    edge.setMaxSpeed(roadAndSpeed.get(wayType));
                    graph.getEdgeMap().put(edge.getId(),edge);
                    graph.getEdges().add(edge);
                }
            }
        }
    }
}

