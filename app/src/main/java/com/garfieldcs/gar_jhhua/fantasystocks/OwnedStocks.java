package com.garfieldcs.gar_jhhua.fantasystocks;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class OwnedStocks {
    private int id; //Name of text file
    private BufferedWriter writeTo;
    private BufferedReader readFrom;
    private Context context;

    private ArrayList<String> info; //Whole string which includes name, price, quantity
    private ArrayList<String> name;
    private ArrayList<Double> price;
    private ArrayList<Integer> quantity;

    public OwnedStocks(int id, Context context) {
        this.id = id;
        this.context = context;
        info = new ArrayList<String>();
        name = new ArrayList<String>();
        price = new ArrayList<Double>();
        quantity = new ArrayList<Integer>();

        try {
            FileOutputStream fos = context.openFileOutput(id + ".txt", 0);
            writeTo = new BufferedWriter(new OutputStreamWriter(fos));
            FileReader reader = new FileReader(new File(context.getFilesDir(), id + ".txt"));
            readFrom = new BufferedReader(reader);
            fillArrays();
        } catch (IOException e) {
            System.out.println("Something wrong went with the files");
            e.printStackTrace();
        }
    }

    //Fills the arrays with info from a file
    private void fillArrays() throws IOException {
        refresh();
        String infoString = readFrom.readLine();
        while (infoString != null) {
            info.add(infoString);
            infoString = readFrom.readLine();
        }
        for (String i : info) {
            Scanner temp = new Scanner(i);
            name.add(temp.next());
            price.add(temp.nextDouble());
            quantity.add(temp.nextInt());
        }
        readFrom.close();
    }

    public int getSize() {
        return info.size();
    }

    public String getAsset(int index) {
        return info.get(index);
    }

    public ArrayList<String> getAsset() {
        return info;
    }

    public String getAssetName(int index) {
        return name.get(index);
    }

    public ArrayList<String> getAssetName() {
        return name;
    }

    public Double getAssetPrice(int index) {
        return price.get(index);
    }

    public ArrayList<Double> getAssetPrice() {
        return price;
    }

    public Integer getAssetQuantity(int index) {
        return quantity.get(index);
    }

    public ArrayList<Integer> getAssetQuantity() {
        return quantity;
    }

    //Adds a stock and its info to a file
    public void addStock(StockInfo stock, int quantityPurchased) throws IOException {
        writeTo.write(stock.getName() + " " + stock.getRawPrice() + " " + quantityPurchased);
        writeTo.newLine();
        fillArrays();
    }

    //Currently sells all quantities of a purchase of stock (for now)
    public void removeStock(StockInfo stock, int quantityPurchased) throws IOException {
        String removeLine = stock.getName() + " " + stock.getPrice() + " " + quantityPurchased;
        File oldFile = new File(context.getFilesDir(), id + ".txt");
        File oldFileName = oldFile;
        File newFile = new File(context.getFilesDir(), id + "b.txt");
        BufferedReader reader = new BufferedReader(new FileReader(oldFile));
        BufferedWriter writer = new BufferedWriter((new FileWriter(newFile)));
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            if (!currentLine.trim().equals(removeLine)) {
                writer.write(currentLine);
                writer.flush();
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

    public int getID() {
        return id;
    }
}
