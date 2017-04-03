package com.garfieldcs.gar_jhhua.fantasystocks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class OwnedStocks {
    public static final double INITIAL_BANK_VALUE = 20000;

    private int id;
    private BufferedReader bankReadFrom;
    private BufferedReader readFrom;
    private Context context;

    private double bankAssets;
    private double assetValue;
    private double percentValueChange;
    private double rawAssetChange;
    private ArrayList<String> info;
    private ArrayList<String> name;
    private ArrayList<Double> price;
    private ArrayList<Integer> quantity;
    private boolean containStock;

    private StockInfo stock;
    private Double currentPrice;

    public OwnedStocks(int id, Context context) {
        this.id = id;
        this.context = context;
        info = new ArrayList<String>();
        name = new ArrayList<String>();
        price = new ArrayList<Double>();
        quantity = new ArrayList<Integer>();
        containStock = false;

        try {
            fillArrays();
        } catch (IOException e) {
            System.out.println("Something wrong went with the files");
            e.printStackTrace();
        }
    }

    //Fills the arrays with info from a file
    private void fillArrays() throws IOException {
        refresh();
        File read = new File(context.getFilesDir(), "S" + id + ".txt");
        read.createNewFile();
        File bankRead = new File(context.getFilesDir(), "B" + id + ".txt");
        bankRead.createNewFile();
        readFrom = new BufferedReader(new FileReader(read));
        bankReadFrom = new BufferedReader(new FileReader(bankRead));

        String infoString;
        while ((infoString = readFrom.readLine()) != null) {
            System.out.println(infoString);
            containStock = true;
            info.add(infoString);
            System.out.println("InfoString is not null!");
        }
        int count = 0; //For testing
        for (String i : info) {
            Scanner temp = new Scanner(i);
            name.add(temp.next());
            System.out.println(name.get(count));
            price.add(Double.parseDouble(temp.next()));
            System.out.println(price.get(count));
            quantity.add(Integer.parseInt(temp.next()));
            System.out.println(quantity.get(count));
            count++;
        }
        calcBankAssets();
        readFrom.close();
        bankReadFrom.close();
    }

    private void calcBankAssets() {
        PrintWriter writeTo;
        try {
            BufferedReader tempRead = new BufferedReader(new FileReader
                    (new File(context.getFilesDir(), "B" + id + ".txt")));
            String tempLine = tempRead.readLine();
            if (tempLine == null) {
                writeTo = new PrintWriter(new File(context.getFilesDir(), "B" + id + ".txt"));
                writeTo.println(INITIAL_BANK_VALUE);
                bankAssets = INITIAL_BANK_VALUE;
                writeTo.flush();
                writeTo.close();
            } else {
                if (!(tempLine.equals(""))) {
                    bankAssets = Math.round(Double.parseDouble
                            (bankReadFrom.readLine())* 100) / 100.0;
                } else {
                    writeTo = new PrintWriter(new File(context.getFilesDir(), "B" + id + ".txt"));
                    writeTo.println(INITIAL_BANK_VALUE);
                    bankAssets = INITIAL_BANK_VALUE;
                    writeTo.flush();
                    writeTo.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Calculated bank assets!");
    }

    //Calculates various changes in asset value
    public void calcChange() {
        assetValue = 0.0;
        rawAssetChange = 0.0;
        double initialAssetValue = 0.0;
        System.out.println("Calculating value changes");
        for (int i = 0; i < info.size(); i++) {
            System.out.println("Pre-stock");
            stock = new StockInfo(name.get(i), context);
            System.out.println("Post-stock");
            try {
                new StockData().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e ) {
                e.printStackTrace();
            }
            System.out.println("Post-stock");
            double priceChange = price.get(i) - currentPrice;
            rawAssetChange+= priceChange * quantity.get(i);
            assetValue+= currentPrice * quantity.get(i);
            initialAssetValue+= price.get(i) * quantity.get(i);
        }
        percentValueChange = initialAssetValue / assetValue * 100;
        System.out.println("Finished calculating value changes");
        System.out.println(assetValue);
    }

    private void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
        System.out.println(this.currentPrice);
    }

    //Adds a stock and its info to a file
    public void addStock(String symbol, double price, int quantityPurchased) throws IOException {
        PrintWriter writeTo = null;
        try {
            writeTo = new PrintWriter(new File(context.getFilesDir(), "S" + id + ".txt"));
            System.out.println("Printing the added stock");
            String str = symbol + " " + price + " " + quantityPurchased;
            System.out.println(str);
            writeTo.println(str);
            writeTo.flush();
            writeTo = new PrintWriter(new File(context.getFilesDir(), "B" + id + ".txt"));
            writeTo.println(bankAssets - (price * quantityPurchased));
            System.out.println("Stock added!");
            writeTo.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeTo.close();
        }
        fillArrays();
    }

    public void removeStock(String name, double price, int quantitySold) throws IOException {
        File oldFile = new File(context.getFilesDir(), "S" + id + ".txt");
        File oldFileName = oldFile;
        File newFile = new File(context.getFilesDir(), "S" + id + "b.txt");
        newFile.createNewFile();
        BufferedReader tempRead = new BufferedReader(new FileReader(oldFile));
        String currentLine;
        String removeLine = "";
        while (((currentLine = tempRead.readLine()) != null)) {
            Scanner s = new Scanner(currentLine);
            if (s.next().equals(name)) {
                removeLine = currentLine;
            }
        }
        tempRead.close();
        BufferedReader reader = new BufferedReader(new FileReader(oldFile));
        PrintWriter writer = new PrintWriter(newFile);
        while ((currentLine = reader.readLine()) != null) {
            if (!currentLine.equals(removeLine)) {
                writer.println(currentLine);
                writer.flush();
            } else {
                Scanner s = new Scanner(currentLine);
                String tempName = s.next();
                String pricePurchased = s.next();
                int quantity = s.nextInt() - quantitySold;
                if (quantity > 0) {
                    writer.println(tempName + " " + pricePurchased + " " + quantity);
                    writer.flush();
                }
                PrintWriter writeTo = new PrintWriter(new File
                        (context.getFilesDir(), "B" + id + ".txt"));
                writeTo.println( bankAssets + quantitySold * price);
                writeTo.flush();
                writeTo.close();
                break;
            }
        }
        reader.close();
        writer.close();
        oldFile.delete();
        newFile.renameTo(oldFileName);
        fillArrays();
    }

    //Clears all the ArrayLists so they can be refilled
    private void refresh() {
        info.clear();
        name.clear();
        price.clear();
        quantity.clear();
    }

    public double getBankAssets() {
        System.out.println(bankAssets);
        return bankAssets;
    }

    public double getAssetValue() {
        //calcChange();
        assetValue = 0;
        return assetValue;
    }

    public double getRawAssetChange() {
        calcChange();
        return rawAssetChange;
    }

    public double getPercentValueChange() {
        calcChange();
        return percentValueChange;
    }

    public double getTotalAssets() {
        //calcChange();
        return bankAssets + assetValue;
    }

    public int getSize() {
        return info.size();
    }

    public String getAsset(int index) {
        if (containStock) {
            return info.get(index);
        }
        return null;
    }

    public ArrayList<String> getAsset() {
        //name, price, quantity
        //Eventually make a new array with better formatting
        System.out.println(info.toString());
        return info;
    }

    public String getAssetName(int index) {
        if (containStock) {
            return name.get(index);
        }
        return null;
    }

    public ArrayList<String> getAssetName() {
        return name;
    }

    public double getAssetPrice(int index) {
        if (containStock) {
            return price.get(index);
        }
        return 0.0;
    }

    public ArrayList<Double> getAssetPrice() {
        return price;
    }

    public Integer getAssetQuantity(int index) {
        if (containStock) {
            return quantity.get(index);
        }
        return 0;
    }

    public ArrayList<Integer> getAssetQuantity() {
        return quantity;
    }

    public int getID() {
        return id;
    }

    //Just retrieves the stock price from StockInfo for calcChange
    private class StockData extends AsyncTask<Void, Void, Double> {

        @Override
        protected Double doInBackground(Void... params) {
            boolean status = false;
            while (!status) {
                status = stock.getStatus();
            }
            System.out.println(stock.getRawPrice());
            return stock.getRawPrice();
        }

        @Override
        protected void onPostExecute(Double result) {
            setCurrentPrice(result);
            super.onPostExecute(result);
        }
    }
}
