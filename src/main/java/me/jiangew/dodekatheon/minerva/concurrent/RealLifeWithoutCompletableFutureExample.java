package me.jiangew.dodekatheon.minerva.concurrent;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc: xxx
 * <p>
 * Author: Jiangew
 * Date: 04/03/2018
 */
public class RealLifeWithoutCompletableFutureExample {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        List<Car> cars = cars();
        cars.forEach(car -> {
            float rating = rating(car.manufacturerId);
            car.setRating(rating);
        });

        cars.forEach(System.out::println);

        long end = System.currentTimeMillis();

        System.out.println("Took " + (end - start) + " ms.");
    }

    static float rating(int manufacturer) {
        try {
            simulateDelay();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        switch (manufacturer) {
            case 2:
                return 4f;
            case 3:
                return 4.1f;
            case 7:
                return 4.2f;
            default:
                return 5f;
        }
    }

    static List<Car> cars() {
        List<Car> carList = new ArrayList<>();
        carList.add(new Car(1, 3, "Fiesta", 2017));
        carList.add(new Car(2, 7, "Camry", 2014));
        carList.add(new Car(3, 2, "M2", 2008));
        return carList;
    }

    private static void simulateDelay() throws InterruptedException {
        Thread.sleep(5000);
    }

    static class Car {
        int id;
        int manufacturerId;
        String model;
        int year;
        float rating;

        public Car(int id, int manufacturerId, String model, int year) {
            this.id = id;
            this.manufacturerId = manufacturerId;
            this.model = model;
            this.year = year;
        }

        void setRating(float rating) {
            this.rating = rating;
        }

        @Override
        public String toString() {
            return "Car (id=" + id + ", manufacturerId=" + manufacturerId + ", model=" + model + ", year=" + year
                   + ", rating=" + rating;
        }
    }
}
