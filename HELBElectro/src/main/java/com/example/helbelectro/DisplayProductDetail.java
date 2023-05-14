package com.example.helbelectro;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DisplayProductDetail {
    private static DisplayProductDetail instance;

    // constructeur prive pour empecher l'instanciation directe
    DisplayProductDetail() {
    }

    // methode statique pour obtenir l'instance unique du singleton
    public static DisplayProductDetail getInstance() {
        if (instance == null) {
            instance = new DisplayProductDetail();
        }
        return instance;
    }

    // int pour les statistiques chacun correspont a son propre produit
    // pour pouvoir calculer chaque produit separement
    // ici c'est pour d'abord pour calculer le nombre de vente
    // autant de fois que un ticket sera generer
    // ensuite pour calculer le nombre de produit que chaque bouton a posseder
    private  int sellCount = 0;
    private  int statNumberBattery = 0;
    private  int statNumberSensor = 0;
    private  int statNumberMotor = 0;
    private  int statNumberAlarm = 0;
    private  int statNumberCar = 0;
    private  int statNumberRobot = 0;
    private  int statNumberDrone = 0;
    private int padding = 20;
    private int space = 10;
    int widthScene = 400;
    int heightScene = 350;


    // methode qui prend l'evenement du bouton cliqué
    public void onButtonProductClicked(ActionEvent event) {
        Button bt_productFinish = (Button) event.getSource();
        int rowIndex = GridPane.getRowIndex(bt_productFinish) - 1;
        int columnIndex = GridPane.getColumnIndex(bt_productFinish) - 1;
        // j'ajoute au titre l'emplacements du bouton
        String emplacements = "Emplacements (" + rowIndex + ", " + columnIndex + ")";
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle(emplacements);
        // j'ajoute a product les données du bouton
        Product product = (Product) bt_productFinish.getUserData();
        // si c'est nul on apel la methode vide
        // sinon ce sera la methode avec les stats et la vente
        if (product == null) {
            showEmptyProductModal(modal);
        } else {
            showProductModal(modal, product, bt_productFinish);
        }
    }

    // lorsqu on appuie sur un bouton de la Grid
    // methode pour l'affichage VIDE  de ma fenetre modal
    private void showEmptyProductModal(Stage modal) {
        Label statut = new Label("Statut : inoccupé");
        VBox vbox = new VBox(statut);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(space);
        vbox.setPadding(new Insets(padding));

        modal.setScene(new Scene(vbox, widthScene, heightScene));
        modal.showAndWait();
    }
    // sinon ce sera l'affichage avec la vente et les stats
    private void showProductModal(Stage modal, Product product, Button bt_productFinish) {
        // petit label qui affiche le statut
        Label statut = new Label("Statut : occupé");
        // on va recuper les données de chaque produits
        Label type = new Label("Type de produit: " + product.getnameForScene());
        Label price = new Label("Prix : " + product.getSellingPrice() + " euros");
        Label ecoScore = new Label("Eco-Score : " + product.getEcoScore());
        Button statsButton = statsPtoduct(product,modal);
        Button sellButton = sellProduct(product, bt_productFinish, modal);

        VBox vbox = new VBox(statsButton, statut, type, price, ecoScore);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(space);
        vbox.setPadding(new Insets(padding));
            //obliger de verifier quel type de produits c'est
        // car il faut savoir combien et quel label ajouter a la vbox de ma fenettre
        // par exemple pour le drone il va ajouter les labels pour tout les attribut des composant
        // car le drone est consitutuer des 3 composants
        if (product instanceof ProductSensor) {
            statNumberSensor++;
            addSensorLabels(vbox);
        } else if (product instanceof ProductBattery) {
            statNumberBattery++;
            addBatteryLabel(vbox);
        } else if (product instanceof ProductMotor) {
            statNumberMotor++;
            addMotorLabel(vbox);
        } else if (product instanceof ProductDrone) {
            statNumberDrone++;
            addMotorLabel(vbox);
            addSensorLabels(vbox);
            addBatteryLabel(vbox);
        } else if (product instanceof ProductCar) {
            statNumberCar++;
            addMotorLabel(vbox);
            addBatteryLabel(vbox);
        } else if (product instanceof ProductAlarm) {
            statNumberAlarm++;
            addBatteryLabel(vbox);
            addSensorLabels(vbox);
        } else if (product instanceof ProductRobot) {
            statNumberRobot++;
            addMotorLabel(vbox);
            addSensorLabels(vbox);
        }
        vbox.getChildren().addAll( sellButton);


        modal.setScene(new Scene(vbox, widthScene, heightScene));
        modal.showAndWait();
    }

    private void addSensorLabels(VBox vbox) {
        Label rangeLabel = new Label("Range: " + ComponentSensor.getRange());
        Label colorSensorLabel = new Label("Color Sensor: " + ComponentSensor.getColorSensor());
        vbox.getChildren().addAll(rangeLabel, colorSensorLabel);
    }
    private void addBatteryLabel(VBox vbox) {
        Label loadLabel = new Label("Load: " + ComponentBattery.getLoad());
        vbox.getChildren().add(loadLabel);
    }
    private void addMotorLabel(VBox vbox) {
        Label powerLabel = new Label("Power: " + ComponentMotor.getPower());
        vbox.getChildren().add(powerLabel);
    }

    // methode pour créer le bouton des stats
    private Button statsPtoduct(Product product, Stage modal) {
        Button statsButton = new Button("Voir les statistiques de cet emplacement");
        statsButton.setStyle("-fx-background-color: #3f7ad9; -fx-text-fill: white;");

        // lorsque le bouton sera cliquer toute la logique sera applliqué

        statsButton.setOnAction(e -> {
            int totalSellCount = sellCount;
            // total des produit dans le bouton
            int totalProductCount = statNumberSensor + statNumberBattery + statNumberMotor + statNumberDrone
                    + statNumberCar + statNumberAlarm + statNumberRobot;

            String contentText = "Nombre total de vente dans cet emplacement : " + totalSellCount + "\n";
            contentText += "Nombre total de produits : " + totalProductCount + "\n";

            // verifie pour chaque produit si il est dans le bouton
            if (statNumberSensor > 0) {
                contentText += "Nombre de produits Sensor : " + statNumberSensor + "\n";
            }
            if (statNumberBattery > 0) {
                contentText += "Nombre de produits Battery : " + statNumberBattery + "\n";
            }
            if (statNumberMotor > 0) {
                contentText += "Nombre de produits Motor : " + statNumberMotor + "\n";
            }
            if (statNumberDrone > 0) {
                contentText += "Nombre de produits Drone : " + statNumberDrone + "\n";
            }
            if (statNumberCar > 0) {
                contentText += "Nombre de produits Car : " + statNumberCar + "\n";
            }
            if (statNumberAlarm > 0) {
                contentText += "Nombre de produits Alarm : " + statNumberAlarm + "\n";
            }
            if (statNumberRobot > 0) {
                contentText += "Nombre de produits Robot : " + statNumberRobot + "\n";
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Statistiques de l'emplacement");
            alert.setHeaderText(null);
            //affiche tous les nombres pour chaque produit
            alert.setContentText(contentText);

            alert.setOnCloseRequest(event -> {
                updateProductCount(product, false);
            });
            alert.showAndWait();
            modal.close();
        });

        return statsButton;
    }

    private void updateProductCount(Product product, boolean increment) {
        // maj le cpt en fonction de l'incrémentation
        // car sinon bug avec les autres boutons, ca se melange
        if (product instanceof ProductSensor) {
            statNumberSensor += increment ? 1 : -1; // ? si increment est true, + 1, sinon - 1
        } else if (product instanceof ProductBattery) {
            statNumberBattery += increment ? 1 : -1;
        } else if (product instanceof ProductMotor) {
            statNumberMotor += increment ? 1 : -1;
        } else if (product instanceof ProductDrone) {
            statNumberDrone += increment ? 1 : -1;
        } else if (product instanceof ProductCar) {
            statNumberCar += increment ? 1 : -1;
        } else if (product instanceof ProductAlarm) {
            statNumberAlarm += increment ? 1 : -1;
        } else if (product instanceof ProductRobot) {
            statNumberRobot += increment ? 1 : -1;
        }
    }

    // method de
    private Button sellProduct(Product product, Button bt_productFinish, Stage modal) {
        Button sellButton = new Button("Vendre produit");
        sellButton.setStyle("-fx-background-color: #0b6517; -fx-text-fill: white;");

        sellButton.setOnAction(e -> {
            // apel la class ticket pour generer la logique du ticket
            Ticket.getInstance().registerSale(product, product.getSellingPrice(), product.getEcoScore());
            HELBElectroController.getInstance().productObjectList.remove(product);

            // nombre de vente pour les stats
            sellCount++;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Vente enregistrée");
            alert.setHeaderText(null);
            alert.setContentText("Le produit a été vendu !");
            alert.showAndWait();
            modal.close();

            bt_productFinish.setStyle("-fx-background-color: white;");
            bt_productFinish.setText("");
        });

        return sellButton;
    }

}
