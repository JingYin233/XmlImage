package com.example;

import com.example.views.LoginViewer;

public class Main {
    public static void main(String[] args) {

        System.setProperty("java.awt.headless", "false");

        new LoginViewer();
    }
}
