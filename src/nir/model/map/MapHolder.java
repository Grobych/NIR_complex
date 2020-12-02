package nir.model.map;

import nir.util.logging.Log;

import java.util.Observable;

public class MapHolder extends Observable{
    public static final MapHolder INSTANCE = new MapHolder();

    private LevelMap levelMap;
    private ImageMap imageMap;
    public void init(){
        this.levelMap = new LevelMap();
        this.imageMap = new ImageMap();
    }
    public LevelMap getLevelMap() {
        return levelMap;
    }
    public ImageMap getImageMap() {
        return imageMap;
    }

    public void loadMap(String fileName){
        Log.debug("loading map  " + fileName);
        this.levelMap.loadMapFromImage(fileName);
        this.imageMap.setFileName(fileName);
        setChanged();
        notifyObservers();
        Log.debug(hasChanged());
    }
}
