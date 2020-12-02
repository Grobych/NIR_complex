package nir.controller;

import com.sun.xml.internal.ws.Closeable;
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
import nir.model.map.MapHolder;
import nir.threads.RenderThread;
import nir.util.logging.Log;
import org.locationtech.jts.geom.Coordinate;

import javax.xml.ws.WebServiceException;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ImageController implements Initializable, Closeable {
    private RenderThread renderThread;
    public ImageController getInstance(){
        return this;
    }
    @FXML
    Canvas mapCanvas, robotCanvas, utilCanvas;
    @FXML
    Button loadMapButton, setStartPointButton, setEndPointButton;
    @FXML
    VBox ap;
    @FXML
    Pane pane;
    @FXML
    ScrollPane sp;

    private boolean isStartPointing = false;
    private boolean isEndPointing = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Log.info("Map init...");
        MapHolder.INSTANCE.init();
        Log.info("done.");

        Log.info("Render thread init...");
        renderThread = new RenderThread();
        renderThread.setCanvases(mapCanvas, robotCanvas, utilCanvas);
        MapHolder.INSTANCE.addObserver(renderThread);
        renderThread.start();
        sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onMouseClicked(event);
            }
        });
        Log.info("Done");
    }

    public void stop() {
    }

    @Override
    public void close() throws WebServiceException {
        if (renderThread != null) renderThread.stopThread();

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
    }
    @FXML
    public void setEndPointButtonClick(){
        this.isEndPointing = true;
        this.isStartPointing = false;
    }
    @FXML
    public void onMouseClicked(MouseEvent event){
        Coordinate c = new Coordinate(event.getSceneX(), event.getSceneY());
        Log.debug(c);
//        if (isStartPointing) {
//
//        }
    }
}
