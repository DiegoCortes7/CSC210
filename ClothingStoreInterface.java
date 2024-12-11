/**
 * Clothing Store Management System
 * Stable Release Version
 * Diego Cortes and Aboubcar Traore
 * CSC210
 * November 28, 2024
 * JavaFX Interface with Multiple Item Purchases and a Remove Button in.(Tried to make a X Button)
 * In order to purchase an item you need to click the Add Item button and just go from there. Like
 * insert the Item Name and Quantity and if you want more Items you just click the Add Item button
 * again and it works. Also the logout button for realism
 */
package com.example.clothingstoreproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClothingStoreInterface extends Application {
    private ClothingStore store = new ClothingStore();
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Clothing Store");

        // Show the login scene
        Scene loginScene = createLoginScene();
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
    //This is the Login Scene with the VBox
    private Scene createLoginScene() {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        //My Button for Login
        Button loginButton = new Button("Login");
        Label errorLabel = new Label();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (store.validateCredentials(username, password)) {
                primaryStage.setScene(createStoreScene());
            } else {
                errorLabel.setText("Invalid credentials");
            }
        });
        //This is all the child nodes
        loginLayout.getChildren().addAll(
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                loginButton,
                errorLabel
        );

        return new Scene(loginLayout, 300, 250);
    }
    //This is the Store Scene
    private Scene createStoreScene() {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));

        // Available Items Label
        Label itemsLabel = new Label("Available Items:");

        // Item List Grid
        GridPane itemGrid = createItemListGrid();

        // Purchase Section
        Label purchaseLabel = new Label("Create Purchase:");

        // Scroll Pane for item purchases
        ScrollPane purchaseScrollPane = new ScrollPane();

        // Purchase Items Container
        VBox itemInputContainer = new VBox(10);

        // Result Label
        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        // Add Item Button
        Button addItemButton = new Button("Add Item");
        addItemButton.setOnAction(e -> addItemInputRow(itemInputContainer));

        // Calculate Total Button
        Button calculateButton = new Button("Calculate Total");
        calculateButton.setOnAction(e -> calculatePurchase(itemInputContainer, resultLabel));

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> primaryStage.setScene(createLoginScene()));

        // Layout Setup
        purchaseScrollPane.setContent(itemInputContainer);
        purchaseScrollPane.setFitToWidth(true);

        mainLayout.getChildren().addAll(
                itemsLabel,
                itemGrid,
                purchaseLabel,
                addItemButton,
                purchaseScrollPane,
                calculateButton,
                resultLabel,
                logoutButton
        );

        return new Scene(mainLayout, 400, 600);
    }
    //This is my GridPane that is the Item List
    private GridPane createItemListGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        String[] itemNames = store.getItemNames();
        double[] itemPrices = store.getItemPrices();

        for (int i = 0; i < itemNames.length; i++) {
            Label nameLabel = new Label(itemNames[i]);
            Label priceLabel = new Label(String.format("$%.2f", itemPrices[i]));

            grid.add(nameLabel, 0, i);
            grid.add(priceLabel, 1, i);
        }

        return grid;
    }

    private void addItemInputRow(VBox container) {
        HBox itemRow = new HBox(10);

        // Item Name TextField
        TextField itemField = new TextField();
        itemField.setPromptText("Enter item name");

        // Quantity TextField
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        // Remove Button
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> container.getChildren().remove(itemRow));

        itemRow.getChildren().addAll(itemField, quantityField, removeButton);
        container.getChildren().add(itemRow);
    }
    //This is where we calculate the Purchases
    private void calculatePurchase(VBox container, Label resultLabel) {
        List<ClothingStore.PurchaseItem> purchaseItems = new ArrayList<>();

        //This is where we collect items and quantities
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof HBox) {
                HBox itemRow = (HBox) node;
                TextField itemField = (TextField) itemRow.getChildren().get(0);
                TextField quantityField = (TextField) itemRow.getChildren().get(1);

                String itemName = itemField.getText().trim();
                String quantityText = quantityField.getText().trim();

                if (!itemName.isEmpty() && !quantityText.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityText);
                        purchaseItems.add(new ClothingStore.PurchaseItem(itemName, quantity));
                    } catch (NumberFormatException ex) {
                        resultLabel.setText("Invalid quantity for " + itemName);
                        return;
                    }
                }
            }
        }

        // Calculate purchases
        ClothingStore.PurchaseResult result = store.calculateMultiItemPurchase(purchaseItems);

        if (result.isValid()) {
            StringBuilder resultText = new StringBuilder();
            resultText.append("Purchase Breakdown:\n");

            //The Item breakdown
            for (Map.Entry<String, Double> entry : result.getItemBreakdown().entrySet()) {
                resultText.append(String.format("%s: $%.2f\n", entry.getKey(), entry.getValue()));
            }

            // Totals
            resultText.append(String.format("\nPre-tax Total: $%.2f\n", result.getPreTaxTotal()));
            resultText.append(String.format("Tax: $%.2f\n", result.getTax()));
            resultText.append(String.format("Total Cost: $%.2f", result.getTotalCost()));

            resultLabel.setText(resultText.toString());
        } else {
            resultLabel.setText(result.getErrorMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}