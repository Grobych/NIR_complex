package nir.controller;

import com.sun.xml.internal.ws.Closeable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import nir.model.map.MapHolder;
import nir.threads.RenderThread;
import nir.util.logging.Log;

import javax.xml.ws.WebServiceException;
import java.net.URL;
import java.util.ResourceBundle;

public class ImageController implements Initializable, Closeable {
    private RenderThread renderThread;
    public ImageController getInstance(){
        return this;
    }
    @FXML
    Canvas mapCanvas, robotCanvas, utilCanvas;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Log.info("Map init...");
        MapHolder.INSTANCE.init();
        Log.info("done.");

        renderThread = new RenderThread();
        renderThread.setCanvases(mapCanvas, robotCanvas, utilCanvas);
        renderThread.start();
    }

    public void stop() {
    }

    @Override
    public void close() throws WebServiceException {
        if (renderThread != null) renderThread.stopThread();

    }
}
