/**
 * Clothing Store Project
 * Stable Release Version
 * Diego Cortes and Aboubcar Traore
 * CSC210
 * November 28, 2024
 * This is my main class where we made Clothing Store with Multiple Purchases Selections possible
 * Also a logout button is in to make it realisitic to a actual clothing store webpage.
 */
package com.example.clothingstoreproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClothingStore {
    // Constants
    private static final double TAX_RATE = 0.08;
    private static final int CREDENTIAL_USERNAME_INDEX = 0;
    private static final int CREDENTIAL_PASSWORD_INDEX = 1;

    // Store data
    private final String[][] credentials;
    private final String[] itemNames;
    private final double[] itemPrices;

    //THis is the PurchaseItem inner class
    public static class PurchaseItem {
        private String itemName;
        private int quantity;
        private double itemTotal;

        public PurchaseItem(String itemName, int quantity) {
            this.itemName = itemName;
            this.quantity = quantity;
        }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getItemTotal() { return itemTotal; }
        public void setItemTotal(double itemTotal) { this.itemTotal = itemTotal; }
    }

    // Constructor
    public ClothingStore() {
        this.credentials = new String[][]{{"Macys", "1 Herald Square"}};
        this.itemNames = new String[]{"T-Shirt", "Jeans", "Jacket", "Shoes"};
        this.itemPrices = new double[]{19.99, 49.99, 79.99, 59.99};
    }

    // This validate all my credentials
    public boolean validateCredentials(String inputUsername, String inputPassword) {
        return inputUsername.equals(getUsername()) &&
                inputPassword.equals(getPassword());
    }

    //my Getter Methods
    public String getUsername() {
        return credentials[0][CREDENTIAL_USERNAME_INDEX];
    }

    public String getPassword() {
        return credentials[0][CREDENTIAL_PASSWORD_INDEX];
    }

    public String[] getItemNames() {
        return itemNames.clone();
    }

    public double[] getItemPrices() {
        return itemPrices.clone();
    }

    //This is for the Price Retrieval
    public double getItemPrice(String itemName) {
        if (itemName == null) return -1;

        for (int i = 0; i < itemNames.length; i++) {
            if (itemNames[i].equalsIgnoreCase(itemName)) {
                return itemPrices[i];
            }
        }
        return -1;
    }

    // Multi Item Purchase Method (To buy as many items you want)
    public PurchaseResult calculateMultiItemPurchase(List<PurchaseItem> items) {
        if (items == null || items.isEmpty()) {
            return new PurchaseResult(-1, "No items selected");
        }

        double totalPreTax = 0;
        Map<String, Double> itemBreakdown = new HashMap<>();

        // Calculate pre-tax cost for each item
        for (PurchaseItem item : items) {
            String itemName = item.getItemName();
            int quantity = item.getQuantity();

            // Validate item and quantity
            if (quantity <= 0) {
                return new PurchaseResult(-1, "Invalid quantity for " + itemName);
            }

            double itemPrice = getItemPrice(itemName);
            if (itemPrice <= 0) {
                return new PurchaseResult(-1, "Invalid item: " + itemName);
            }

            double itemTotal = itemPrice * quantity;
            totalPreTax += itemTotal;
            itemBreakdown.put(itemName, itemTotal);
            item.setItemTotal(itemTotal);
        }

        // Calculate tax and total
        double tax = calculateTax(totalPreTax);
        double totalWithTax = totalPreTax + tax;

        return new PurchaseResult(totalWithTax, itemBreakdown, totalPreTax, tax);
    }

    // Existing tax calculation method
    public double calculateTax(double cost) {
        return cost > 0 ? cost * TAX_RATE : -1;
    }

    //This is the result wrapper class for multi-item purchases
    public static class PurchaseResult {
        private final double totalCost;
        private final Map<String, Double> itemBreakdown;
        private final double preTaxTotal;
        private final double tax;
        private final String errorMessage;

        //This is the constructor for successful purchases
        public PurchaseResult(double totalCost, Map<String, Double> itemBreakdown,
                              double preTaxTotal, double tax) {
            this.totalCost = totalCost;
            this.itemBreakdown = itemBreakdown;
            this.preTaxTotal = preTaxTotal;
            this.tax = tax;
            this.errorMessage = null;
        }

        // This constructor is for errors of anykind
        public PurchaseResult(double totalCost, String errorMessage) {
            this.totalCost = totalCost;
            this.itemBreakdown = null;
            this.preTaxTotal = -1;
            this.tax = -1;
            this.errorMessage = errorMessage;
        }

        //This is all the Getters
        public double getTotalCost() { return totalCost; }
        public Map<String, Double> getItemBreakdown() { return itemBreakdown; }
        public double getPreTaxTotal() { return preTaxTotal; }
        public double getTax() { return tax; }
        public String getErrorMessage() { return errorMessage; }
        public boolean isValid() { return errorMessage == null; }
    }
}