package com.garfieldcs.gar_jhhua.fantasystocks;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class User {

    private int id;
    private String username;
    private String password;
    private String tempUser;
    private String tempPass;
    private boolean doesExist;
    private boolean doesNameExist;

    private Context context;

    private ArrayList<Integer> ids;

    public User(String username, String password, boolean createUser, Context context) {
        this.context = context;
        tempUser = username;
        tempPass = password;

        File folder = new File(context.getFilesDir().getAbsolutePath());
        File[] allFiles = folder.listFiles();
        doesExist = false;
        doesNameExist = false;

        checkExist(allFiles);
        makeUser(createUser);
    }

    //Checks to see if the user already exists
    private void checkExist(File[] allFiles) {
        ArrayList<String> usernames = new ArrayList<String>();
        ArrayList<String> passwords = new ArrayList<String>();
        ids = new ArrayList<Integer>();
        System.out.println(allFiles.length);
        try {
            for (int i = 0; i < allFiles.length; i++) {
                if (allFiles[i].isFile()) {
                    System.out.println(1);
                    BufferedReader reader = new BufferedReader(new FileReader(allFiles[i]));
                    String currentLine = reader.readLine();
                    System.out.println(currentLine);
                    while((currentLine = reader.readLine()) != null) {
                        System.out.println(2);
                        Scanner s = new Scanner(currentLine);
                        usernames.add(s.next());
                        System.out.println(usernames.get(i));
                        passwords.add(s.next());
                        System.out.println(passwords.get(i));
                        ids.add(s.nextInt());
                        System.out.println(ids.get(i));
                    }
                    System.out.println(3);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(ids.toString());
        for (int i = 0; i < usernames.size(); i++) {
            if (usernames.get(i).equals(username)) {
                doesNameExist = true;
            }
            if (usernames.get(i).equals(username) && passwords.get(i).equals(password)) {
                this.username = usernames.get(i);
                this.password = passwords.get(i);
                this.id = ids.get(i);
                System.out.println("USER EXISTS");
                doesExist = true;
            }
        }
    }

    //Creates the user if it is called for by MainActivity
    private void makeUser(boolean createUser) {
        if (!doesExist && createUser) {
            boolean generatedID = false;
            while (!generatedID) {
                id = (int) (Math.random() * 1000);
                if (ids.size() == 0) {
                    generatedID = true;
                }
                for (int i : ids) {
                    if (id == i) {
                        continue;
                    }
                    generatedID = true;
                }
            }
            System.out.println("ID GENERATED");
            if (!doesNameExist) {
                this.username = tempUser;
                this.password = tempPass;
                try {
                    PrintWriter writer = new PrintWriter(
                            new File(context.getFilesDir(), id + ".txt"));

                    /*FileOutputStream fos = context.openFileOutput(id + ".txt", context.MODE_PRIVATE);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));*/
                    System.out.println(id + " " + username + " " + password);
                    writer.println(id + " " + username + " " + password);
                    writer.println("end");
                    System.out.println("User created");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                doesExist = true;
            } else {
                throw new IllegalArgumentException("User already exists");
            }
        } else if (doesExist && createUser) {
            throw new IllegalArgumentException("User already exists");
        }
    }

    //Returns and sees if the user exists
    public boolean doesExist() {
        return doesExist;
    }

    public String getUserName() {
        //Add some checks
        return username;
    }

    //For testing only, will delete later
    protected String getPassword() {
        return password;
    }

    public int getID() {
        return id;
    }

}
