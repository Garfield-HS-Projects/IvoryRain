package com.garfieldcs.gar_jhhua.fantasystocks;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CalcChange {

    private static double assetValue;
    private static double rawAssetChange;
    private static double initialAssetValue;
    private static double percentValueChange;
    private static double bankAssets;

    private boolean status;

    private OwnedStocks ownedStocks;
    private MultiStockInfo multi;

    //Handles all the stocks of a user
    public CalcChange(MultiStockInfo multi, OwnedStocks ownedStocks) {
        this.multi = multi;
        this.ownedStocks = ownedStocks;
        assetValue = 0.0;
        rawAssetChange = 0.0;
        initialAssetValue = 0.0;
        percentValueChange = 0.0;
    }

    public void execute() {
        try {
            new StockValueData().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        status = true;
    }

    //Add formatting later to accessor methods

    public double getAssetValue() {
        return assetValue;
    }

    public double getRawAssetChange() {
        return rawAssetChange;
    }

    public double getInitialAssetValue() {
        return initialAssetValue;
    }

    public double getPercentValueChange() {
        return percentValueChange;
    }

    public double getTotalAssetValue() {
        return assetValue + bankAssets;
    }

    public boolean getStatus() {
        return status;
    }

    //Calculates user's asset values
    private class StockValueData extends AsyncTask<Void, Void, Void> {
        ArrayList<Double> price;
        ArrayList<Double> allPrices;
        ArrayList<Integer> quantity;

        @Override
        protected Void doInBackground(Void... params) {
            price = ownedStocks.getAssetPrice();
            allPrices = multi.getAllPrices();
            quantity = ownedStocks.getAssetQuantity();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            bankAssets = ownedStocks.getBankAssets();

            System.out.println("pre calc");
            for (int i = 0; i < allPrices.size(); i++) {
                double currentPrice = allPrices.get(i);
                double priceChange = price.get(i) - currentPrice;
                rawAssetChange+= priceChange * quantity.get(i);
                assetValue+= currentPrice * quantity.get(i);
                initialAssetValue+= price.get(i) * quantity.get(i);
            }
            System.out.println(rawAssetChange + " " + assetValue + " " + initialAssetValue);

            percentValueChange = initialAssetValue / assetValue * 100;
            System.out.println("post calc");
        }
    }
}
