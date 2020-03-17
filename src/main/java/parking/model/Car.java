package parking.model;

public class Car {
    private String numberCar;

    public Car(String numberCar) {
        this.numberCar = numberCar;
    }

    public void setNumberCar(String numberCar) {
        this.numberCar = numberCar;
    }

    public String getNumberCar() {
        return numberCar;
    }

    @Override
    public String toString() {
        return "Car{" +
                "numberCar='" + numberCar + '\'' +
                '}';
    }
}
