package nir.model.base;

import nir.model.list.CargoList;
import org.locationtech.jts.geom.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class RobotCargo extends Robot {

    private int capacity;
    private List<Cargo> cargos = new LinkedList<>();

    public RobotCargo(Coordinate coordinate, int capacity) {
        super(coordinate);
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public int getEmptySpace(){
        return capacity - cargos.size();
    }
    public boolean isFull(){
        return (capacity - cargos.size() == 0);
    }

    public void addCargo(Cargo cargo){
        if (getEmptySpace() > 0) cargos.add(cargo);
    }
    public void removeCargo(Cargo cargo){
        if (cargos.contains(cargo)) cargos.remove(cargo);
    }

    @Override
    public void checkCargo(){
        for (Cargo cargo : CargoList.cargoList) {
            if ((this.getPosition().distance(cargo.getPosition()) < 5) && (!isFull())) {
                addCargo(cargo);
                cargo.setTaken(true);
            }
            if (this.getPosition().distance(cargo.getGoal().getPosition()) < 5) {
                removeCargo(cargo);
                cargo.setDelivered(true);
                cargo.setPosition(this.getPosition());
            }
        }
    }

}
