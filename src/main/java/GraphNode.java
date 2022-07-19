public class GraphNode {
    private int id;
    private double lat;
    private double lon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    // конструктор для вершины на карте
    public GraphNode(int id, double lat, double lon) {
        setId(id);
        setLat(lat);
        setLon(lon);
    }
    //конструктор для создания мнимой вершины из заданных пользователем координат
    public GraphNode(double lat, double lon){
        setLat(lat);
        setLon(lon);
    }


    public String getWKTCoordinates() {
        return lon + " " + lat;
    }

    @Override
    public String toString() {
        return lat + "," + lon;
    }
}
