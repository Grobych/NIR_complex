package nir.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import nir.model.algorithms.BaseRouting;
import nir.model.algorithms.antmodel.AntRouting;
import nir.model.algorithms.beemodel.BeeRouting;
import nir.model.algorithms.swarmmodel.SwarmRouting;
import nir.model.base.Route;
import nir.model.global.GlobalVariables;
import nir.model.global.VariablesLoader;
import nir.model.map.MapHolder;
import nir.threads.RenderThread;
import nir.model.util.logging.Log;
import org.locationtech.jts.geom.Coordinate;

import java.io.Closeable;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ImageController implements Initializable, Closeable {
    private RenderThread renderThread;
    public ImageController getInstance(){
        return this;
    }
    @FXML
    Canvas mapCanvas, robotCanvas, utilCanvas, routeCanvas;
    @FXML
    Button loadMapButton, setStartPointButton, setEndPointButton, getRouteButton;
    @FXML
    VBox ap;
    @FXML
    Pane pane;
    @FXML
    ScrollPane sp;

    public static Coordinate start, end;
    public static Route route;

    private boolean isStartPointing = false;
    private boolean isEndPointing = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Log.info("Map init...");
        MapHolder.INSTANCE.init();
        Log.info("done.");

        Log.info("Render thread init...");
        renderThread = new RenderThread();
        renderThread.setCanvases(mapCanvas, robotCanvas, utilCanvas, routeCanvas);
        MapHolder.INSTANCE.addObserver(renderThread);
        renderThread.start();
        utilCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onMouseClicked(event);
            }
        });
        Log.info("Done");
    }

    public void stop() {
        if (renderThread != null) renderThread.stopThread();
    }

    @Override
    public void close() {
        stop();
    }

    public void loadMapButtonClick(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(ap.getScene().getWindow());
        MapHolder.INSTANCE.loadMap(file.getAbsolutePath());
    }

    @FXML
    public void setStartPointButtonClick(){
        this.isStartPointing = true;
        this.isEndPointing = false;
        getRouteButton.setDisable(true);
        setEndPointButton.setDisable(true);
    }
    @FXML
    public void setEndPointButtonClick(){
        this.isEndPointing = true;
        this.isStartPointing = false;
        getRouteButton.setDisable(true);
        setStartPointButton.setDisable(true);
    }
    @FXML
    public void onMouseClicked(MouseEvent event){
        Coordinate c = new Coordinate(event.getX(), event.getY() );
//        Log.debug(c);
        if (isStartPointing) {
            start = c;
            isStartPointing = false;
            getRouteButton.setDisable(false);
            setEndPointButton.setDisable(false);
        }
        if (isEndPointing){
            end = c;
            isEndPointing = false;
            getRouteButton.setDisable(false);
            setStartPointButton.setDisable(false);
        }
    }

    @FXML
    public void getRouteButtonClick(){
        route = null;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (start!= null && end!=null){
                    try {
                        BaseRouting routing;
                        switch (AlgorithmChooserController.getType()){
                            case ANT: routing = new AntRouting(start,end,VariablesLoader.get(GlobalVariables.getInstance().antParams)); break;
                            case PART: routing = new SwarmRouting(start,end,VariablesLoader.get(GlobalVariables.getInstance().swarmParams)); break;
                            case BEE: routing = new BeeRouting(start,end, VariablesLoader.get(GlobalVariables.getInstance().beeParams)); break;
                            default: return;
                        }
                        FutureTask<Route> future = new FutureTask<>(routing);
                        new Thread(future).start();
                        route = future.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
