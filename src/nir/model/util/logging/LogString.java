package nir.model.util.logging;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LogString{
    private String log = new String();
    private static PropertyChangeSupport support;

    public LogString() {
        support = new PropertyChangeSupport(this);
    }

    public static void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public static void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setLog(String value) {
        support.firePropertyChange("news", this.log, value);
        this.log = value;
    }
}
