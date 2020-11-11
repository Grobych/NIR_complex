package nir.model.map;

public class MapHolder {
    public static final MapHolder INSTANCE = new MapHolder();

    private LevelMap levelMap;

    public void init(){
        this.levelMap = new LevelMap();
    }

    public LevelMap getLevelMap() {
        return levelMap;
    }
}
