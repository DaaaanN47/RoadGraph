import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class DijkstraAlgorithm {

    Queue<VisitedVertex> vertexQueue = new PriorityQueue<>();
    // небольшое нововведение в алгоритм тут мы сейчас работает уже не конкретно с лдистанцией ребра а с ее весом,
    // что может быть и дистацией и временем, но чтобы выводить значение пройденного расстояния  в случае если в качестве веса ребра было выбрано время,
    // отдельно ведется подсчет пройденного расстояния в не зависимости было ли выбрано вреям или дистанция в качестве веса ребра
    public PathContainer CheckVertexes(Graph graph, GraphNode start, GraphNode finish){
        VisitedVertex strVisitedVertex = new VisitedVertex(start.getId(), true);
        VisitedVertex finVisitedVertex = new VisitedVertex(finish.getId());
        vertexQueue.add(strVisitedVertex);
        PathContainer pathContainer = new PathContainer();
        pathContainer.getVisitedpathVertexMap().put(strVisitedVertex.getId(),strVisitedVertex);
        //добавили первую точку
        // в очередь о ваозрастанию,
        //далле пробегаемся по очереди в которой пока один элемент но во время итерации нужно добавить точки которые имеют общее ребро с текущей
        //точка найдена далее она проверяется на то есть ли она в очереди или нет, если ее там нет то проверяется,
        // является ли текущее расстояние от старта меньше чем записанное в вершине, если да то добавляем точку в очередь,
        // если такая точка уже есть в очереди то сравнивается расстояние, записанное в точке в очереди,
        // с расстоянием в рассматриваемой вершине + вес ребра, если оно второе меньше то мы в объекте меняем старые значения на новые.
        //остановкой цикла будет то, что расстояние записанное в рассмотримаемой вершине из очереди будет больше чем расстояние в объекте финиша.
        while(true){
            VisitedVertex vertex = vertexQueue.poll();
            // проверка на то что конечная точка содержится в списке путей и то что вес в текущей точке больше чем в конечной точке
            if(pathContainer.getVisitedpathVertexMap().containsKey(finVisitedVertex.getId()) && vertex.getEdgeWeightsFromStart() >  pathContainer.getVisitedpathVertexMap().get(finVisitedVertex.getId()).getEdgeWeightsFromStart()){
                break;
            } else {
                    graph.getVertexesAndItsEdges().get(vertex.getId()).forEach(edgeId->{

                    Edge edge = graph.edgeMap.get(edgeId);
                    int otherEdgeNodeId = edge.getOtherNode(vertex.getId());
                    VisitedVertex otherVertex;
                    //спрашиваем есть ли такая точка в списке посещенных точек
                    if(pathContainer.getVisitedpathVertexMap().containsKey(otherEdgeNodeId)){
                        otherVertex  = pathContainer.getVisitedpathVertexMap().get(otherEdgeNodeId);
                    } else {
                        otherVertex = new VisitedVertex(otherEdgeNodeId);
                    }
                    //Vertex otherEdgeSide = graph.getVertexMap().get(edge.getOtherNode(vertex.getId()));
                    double currentWeight = otherVertex.getEdgeWeightsFromStart();
                    double newWeight = vertex.getEdgeWeightsFromStart() + edge.getWeight();
                    double edgeLength = vertex.getDistWeightFromStart() + edge.getLength();
                    if(!vertexQueue.contains(otherVertex)){
                        //проверяю является ли расстояние в расмотриваемой точке больше чем в предыдущей точке + вес ребра между ними
                        if(currentWeight>newWeight){
                            vertexQueue.add(otherVertex);
                            otherVertex.setEdgeWeightsFromStart(newWeight);
                            otherVertex.setPrevVertexId(vertex.getId());
                            otherVertex.setDistWeightFromStart(edgeLength);
                            pathContainer.addOrChangeVertexPath(otherVertex);
                        }
                    } else { //если такая точка уже есть в очереди
                        if(currentWeight>newWeight){
                            otherVertex.setPrevVertexId(vertex.getId());
                            otherVertex.setEdgeWeightsFromStart(newWeight);
                            otherVertex.setDistWeightFromStart(edgeLength);
                            pathContainer.addOrChangeVertexPath(otherVertex);
                        }
                    }
                });
            }
        }
        return pathContainer;
    }
//     public void printPathDetails(Vertex finVertex, boolean edgeWeightType){
//        if(edgeWeightType){
//            System.out.println("Расстояние: " + String.format("%.0f",finVertex.getEdgeWeightsFromStart()) + " м");
//            System.out.println("Потраченное время "+  String.format("%.0f",(finVertex.getEdgeWeightsFromStart()/1000)/60) + " мин если двигаться со скоростью 60 км/ч");
//        } else {
//            System.out.println("Расстояние: " + String.format("%.2f",finVertex.getDistWeightFromStart()) + " м");
//            System.out.println("Потраченное время "+  String.format("%.2f",(finVertex.getEdgeWeightsFromStart()) + " мин "));
//        }
//     }
}
