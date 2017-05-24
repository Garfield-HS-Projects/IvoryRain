package com.garfieldcs.gar_jhhua.fantasystocks.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.garfieldcs.gar_jhhua.fantasystocks.R;
import com.garfieldcs.gar_jhhua.fantasystocks.info.User;
import com.garfieldcs.gar_jhhua.fantasystocks.widget.CalcChange;
import com.garfieldcs.gar_jhhua.fantasystocks.info.MultiStockInfo;
import com.garfieldcs.gar_jhhua.fantasystocks.info.OwnedStocks;

import java.util.ArrayList;
import java.util.List;

public class ShowPortfolioActivity extends AppCompatActivity {
    private User user;
    private OwnedStocks ownedStocks;
    private CalcChange calcChange;
    private String username;
    private String password;
    private MultiStockInfo multi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_portfolio);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("Username");
        password = bundle.getString("Password");
        user = new User(username, password, false, getApplicationContext());
        ownedStocks = new OwnedStocks(user.getID(), getApplicationContext());

        ArrayList<String> namesTemp = ownedStocks.getAssetName();
        System.out.println(namesTemp.toString() + " namesTemp");
        multi = new MultiStockInfo
                (namesTemp.toArray(new String[namesTemp.size()]), getApplicationContext());

        /*
        new LoadingData.execute(); goes after the calcChange line.
        For some reason, LoadingData finishes executing before calcChange does.
        We need to have calcChange finish executing its stuff before LoadingData executes.
        Please look into it.

        Also, it is possible that ownedStocks is being passed into calcChange incorrectly.
         */
        calcChange = new CalcChange(multi, ownedStocks);

        new LoadingData().execute();
    }

    public void goToSearch (View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Username", username);
        bundle.putString("Password", password);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //Based on position in the List
    public void goToStock (View view, int position) {
        String stockName = ownedStocks.getAssetName(position);
        Intent intent = new Intent(this, DisplayStockActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Username", username);
        bundle.putString("Password", password);
        bundle.putString("name", stockName);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void goToLeader (View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Username", username);
        bundle.putString("Password", password);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //Loads the information on a separate thread
    private class LoadingData extends AsyncTask<Void, Void, double[]> {
        ProgressDialog dialog = new ProgressDialog(ShowPortfolioActivity.this);
        double investedAssets;
        double bankAssets;
        double totalAssets;
        double percentChange;
        List<String> stocks;

        //Loading circle bar... thing
        @Override
        protected void onPreExecute() {
            stocks = new ArrayList<>();
            stocks = ownedStocks.getAsset();

            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog = dialog.show(ShowPortfolioActivity.this,
                    "Please wait", "Retrieving data...", true);
            super.onPreExecute();
        }

        //Collect data from OwnedStocks and CalcChange
        @Override
        protected double[] doInBackground(Void... params) {

            /*
            This part simply takes ownedStock.getAsset() and turns it into a usable form.
            It shouldn't be impacting the order of calcChange and LoadingData
             */
            List<String> temp = stocks;
            ArrayList<String> names = multi.getAllNames();
            /*stocks.clear();
            for (int i = 0; i < temp.size(); i++) {
                String n = names.get(i);
                double p;
                int q;

                Scanner scanner = new Scanner(temp.get(i));
                scanner.next();
                p = Double.parseDouble(scanner.next());
                q = Integer.parseInt(scanner.next());

                stocks.add(n + " $" + p + " Quantity: " + q);
            }*/

            bankAssets = ownedStocks.getBankAssets();
            investedAssets = calcChange.getAssetValue();
            totalAssets = calcChange.getTotalAssetValue();
            percentChange = calcChange.getPercentValueChange();

            return new double[] {bankAssets, investedAssets, totalAssets, percentChange};
        }


        //Display the information onto the screen
        @Override
        protected void onPostExecute(double[] result) {
            setContentView(R.layout.activity_show_portfolio);
            ListView list = (ListView) findViewById(R.id.userAssetsList);
            TextView teamName = (TextView) findViewById(R.id.userTeamName);
            TextView totalValue = (TextView) findViewById(R.id.TotalAssetValue);
            TextView bankValue = (TextView) findViewById(R.id.BankAccountValue);
            TextView investedValue = (TextView) findViewById(R.id.InvestedAssetsValue);
            TextView percentValue = (TextView) findViewById(R.id.PercentChangeValue);

            teamName.setText(user.getUserName().toUpperCase());
            bankValue.setText("$" + result[0]);
            investedValue.setText("$" + result[1]);
            totalValue.setText("$" + result[2]);
            percentValue.setText(result[3] + "%");

            //Organizes stocks into a clickable list
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (ShowPortfolioActivity.this, android.R.layout.simple_list_item_1, stocks);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    goToStock(view, position);
                }
            });

            dialog.dismiss();

            super.onPostExecute(result);
        }
    }
}
