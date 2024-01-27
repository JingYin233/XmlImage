package com.example.views;

import com.example.utils.XMLParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.toedter.calendar.JDateChooser;

public class LoginViewer {
    private JFrame frame;
    private JTextField filePathField;
    private JTextField dateField;
    private JButton confirmButton;
    private JButton browseButton;

    public LoginViewer() {
        frame = new JFrame("Login");
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        Font font = new Font("宋体", Font.PLAIN, 20);  // 创建一个新的字体对象

        filePathField = new JTextField(30);
        filePathField.setFont(font);  // 设置文本框的字体
        filePathField.setMinimumSize(new Dimension(200, 50));  // 设置文本框的最小尺寸
        filePathField.setPreferredSize(new Dimension(200, 50));  // 设置文本框的首选尺寸
        filePathField.setMaximumSize(new Dimension(200, 50));  // 设置文本框的最大尺寸

        dateField = new JTextField(30);
        dateField.setFont(font);  // 设置文本框的字体
        dateField.setMinimumSize(new Dimension(200, 50));  // 设置文本框的最小尺寸
        dateField.setPreferredSize(new Dimension(200, 50));  // 设置文本框的首选尺寸
        dateField.setMaximumSize(new Dimension(200, 50));  // 设置文本框的最大尺寸

        JLabel filePathLabel = new JLabel("文件路径:");
        filePathLabel.setFont(font);  // 设置标签的字体
        constraints.gridx = 0;
        constraints.gridy = 0;
        frame.add(filePathLabel, constraints);

        constraints.gridx = 1;
        frame.add(filePathField, constraints);

        browseButton = new JButton("浏览");
        browseButton.setPreferredSize(new Dimension(80, 50));  // 修改这行代码来增大按钮的大小
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        constraints.gridx = 2;
        frame.add(browseButton, constraints);

        JLabel dateLabel = new JLabel("修改日期:");
        dateLabel.setFont(font);  // 设置标签的字体
        constraints.gridx = 0;
        constraints.gridy = 1;
        frame.add(dateLabel, constraints);

        constraints.gridx = 1;
        frame.add(dateField, constraints);

        confirmButton = new JButton("确认");
        confirmButton.setFont(font);  // 设置按钮的字体
        confirmButton.setPreferredSize(new Dimension(100, 50));  // 修改这行代码来增大按钮的大小
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                if (filePath == null || filePath.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "文件路径不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String date = dateField.getText();

                if (!date.equals("")) {
                    // 移除字符串两端的空格
                    date = date.trim();

                    // 定义日期和时间的格式
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日，H:m:s");

                    try {
                        // 尝试解析日期和时间
                        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
                        System.out.println("解析成功: " + dateTime);
                    } catch (DateTimeParseException dex) {
                        // 如果解析失败，抛出一个异常
                        JOptionPane.showMessageDialog(null, "时间格式不对", "错误", JOptionPane.ERROR_MESSAGE);
                        throw new IllegalArgumentException("时间格式不对", dex);
                    }
                }

                XMLParser parser = new XMLParser(date);
                parser.parseXMLInFolder(filePath);
                try {
                    parser.parseJPGInFolder(filePath);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                ImageViewer imageViewer = new ImageViewer(parser.getImagePaths(), parser.getLicensePlates(), parser);
                frame.dispose();  // 关闭LoginViewer
                imageViewer.show();
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.CENTER;
        frame.add(confirmButton, constraints);

        frame.pack();
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
