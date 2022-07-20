import java.util.HashSet;
import java.util.Set;

public class OSMNode {
    private int id;
    private double lat;
    private double lon;
    private boolean isCrossRoad;
    Set<Long> waysHasNode = new HashSet<>();
    public boolean isCrossRoad() {
        return isCrossRoad;
    }

    public void setIsCrossRoad(){
        isCrossRoad=true;
    }

    public OSMNode(int id, double lat, double lon) {
        setId(id);
        setLat(lat);
        setLon(lon);
    }
    public OSMNode(int id) {
        setId(id);
    }
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
}
