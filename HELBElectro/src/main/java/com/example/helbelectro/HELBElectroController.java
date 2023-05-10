package com.example.helbelectro;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HELBElectroController {
    private static HELBElectroController instance = null;
    public static List<Object> productObjectList = new ArrayList<>();
    public static List<Product> productObjectListSorted = new ArrayList<>();
    public static ObservableList<Object> componentObjectList = FXCollections.observableArrayList();


    public static HELBElectroController getInstance() {
        if (instance == null) {
            instance = new HELBElectroController();
        }
        return instance;
    }

    public void createComponent(String componentName, String[] values) {
        // Vérifier si le nombre maximal de labels a été atteint
        if (componentObjectList.size() >= HELBElectroView.number_lb_component) {
            System.out.println("Nombre maximal de composant atteint");
        } else {
            // Créer le composant en utilisant la Factory
            Factory factory = new Factory();
            Component component = factory.createComponent(componentName, values);
            componentObjectList.add(component);
            System.out.println("Component " + componentName + " créé");
        }
    }

    public static void createProduct() {
        AtomicBoolean isBusy = new AtomicBoolean(false);
        if (isBusy.get()) {
            return;
        }
        for (Product product : productObjectListSorted) {
            boolean hasAllComponents = hasAllNecessaryComponents(product);
            if (hasAllComponents && !isBusy.get()) {
                int manufacturingDuration = product.getManufacturingDuration();
                System.out.println("Attente de " + manufacturingDuration + " secondes avant de fabriquer " + product.getClass().getSimpleName());
                isBusy.set(true);
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(manufacturingDuration), e -> {
                    Product newProduct = createNewProduct(product);

                    if (newProduct != null) {
                        productObjectList.add(newProduct);
                        System.out.println(newProduct.getClass().getSimpleName() + " créé ");
                    }

                    isBusy.set(false);
                }));

                timeline.play(); // lancer la timeline pour attendre la durée de fabrication du produit

                removeUsedComponents(product);

                break; // arrêter la boucle pour ne fabriquer qu'un seul produit à la fois
            }
        }
    }



    private static boolean hasAllNecessaryComponents(Product product) {
        for (Object componentName : product.getComponentListNecessary()) {
            boolean hasComponent = componentObjectList.stream()
                    .anyMatch(component -> component.getClass().getSimpleName().equals(componentName.getClass().getSimpleName()));
            if (!hasComponent) {
                return false;
            }
        }
        return true;
    }

    private static Product createNewProduct(Product product) {
        if (product instanceof ProductSensor) {
            return new ProductSensor(ComponentSensor.getRange(), ComponentSensor.getColorSensor());
        } else if (product instanceof ProductBattery) {
            return new ProductBattery(ComponentBattery.getLoad());
        } else if (product instanceof ProductMotor) {
            return new ProductMotor(ComponentMotor.getPower());
        } else if (product instanceof ProductDrone) {
            return new ProductDrone(ComponentMotor.getPower(), ComponentSensor.getColorSensor(), ComponentSensor.getRange(), ComponentBattery.getLoad());
        } else if (product instanceof ProductCar) {
            return new ProductCar(ComponentMotor.getPower(), ComponentBattery.getLoad());
        } else if (product instanceof ProductAlarm) {
            return new ProductAlarm(ComponentBattery.getLoad(), ComponentSensor.getColorSensor(), ComponentSensor.getRange());
        } else if (product instanceof ProductRobot) {
            return new ProductRobot(ComponentMotor.getPower(), ComponentSensor.getColorSensor(), ComponentSensor.getRange());
        } else {
            return null;
        }
    }

    private static void removeUsedComponents(Product product) {
        for (Object componentName : product.getComponentListNecessary()) {
            componentObjectList.removeIf(component -> component.getClass().getSimpleName().equals(componentName.getClass().getSimpleName()));
        }
    }

        public static void addProductList() {
        productObjectListSorted.add(new ProductBattery(""));
        productObjectListSorted.add(new ProductSensor("",""));
        productObjectListSorted.add(new ProductMotor(""));
        productObjectListSorted.add(new ProductCar("",""));
        productObjectListSorted.add(new ProductAlarm("","",""));
        productObjectListSorted.add(new ProductDrone("","","",""));
        productObjectListSorted.add(new ProductRobot("","",""));
    }
    public static void getSortedProductListByTime() {
        addProductList();
        productObjectListSorted.sort(Comparator.comparing(Product::getManufacturingDuration));
       // productObjectListSorted.forEach(System.out::println);

    }
    public static void getSortedProductListByScore() {
        addProductList();
        productObjectListSorted.sort(Comparator.comparing(Product::getEcoScore));
       // productObjectListSorted.forEach(System.out::println);

    }
    public static void getSortedProductListByPrice() {
        addProductList();
        productObjectListSorted.sort(Comparator.comparing(Product::getSellingPrice));
        Collections.reverse(productObjectListSorted);
       // productObjectListSorted.forEach(System.out::println);

    }

    public static void getSortedProductListByDiverse() {
        addProductList();
        productObjectListSorted.sort(Comparator.comparingInt(p -> Collections.frequency(productObjectListSorted, p)));
        Collections.reverse(productObjectListSorted);
      //  productObjectListSorted.forEach(System.out::println);

    }



}