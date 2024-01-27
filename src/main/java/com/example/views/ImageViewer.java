package com.example.views;

import com.example.utils.XMLParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageViewer {
    private JFrame frame;
    private JLabel imageLabel;
    private JLabel pageLabel;
    private JLabel licensePlateLabel;
    private JTextField licensePlateInput;
    private List<String> imagePaths;
    private List<String> licensePlates;
    private List<String> userChoices = new ArrayList<>();
    private List<String> modifiedLicensePlates = new ArrayList<>();
    private int currentImageIndex = 0;



    public ImageViewer(List<String> imagePaths, List<String> licensePlates, XMLParser parser) {

        this.imagePaths = imagePaths;
        this.licensePlates = licensePlates;

        // 使用默认值初始化列表
        this.userChoices = new ArrayList<>(Collections.nCopies(imagePaths.size(), null));
        this.modifiedLicensePlates = new ArrayList<>(Collections.nCopies(imagePaths.size(), null));

        frame = new JFrame("Image Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        imageLabel = new JLabel();
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        frame.getContentPane().add(scrollPane, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(500, 70));
        JButton prevButton = new JButton("上一张");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentImageIndex > 0) {
                    currentImageIndex--;
                    updateImage();
                }
            }
        });
        buttonPanel.add(prevButton, BorderLayout.WEST);

        pageLabel = new JLabel();
        pageLabel.setHorizontalAlignment(JLabel.CENTER);
        buttonPanel.add(pageLabel, BorderLayout.CENTER);

        JButton nextButton = new JButton("下一张");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentImageIndex < imagePaths.size() - 1) {
                    currentImageIndex++;
                    updateImage();
                }
            }
        });
        buttonPanel.add(nextButton, BorderLayout.EAST);

        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        Font font = new Font("宋体", Font.PLAIN, 30);  // 创建一个新的字体对象

        licensePlateLabel = new JLabel();
        licensePlateLabel.setFont(font);  // 设置标签的字体
        licensePlateLabel.setHorizontalAlignment(JLabel.CENTER);
        licensePlateLabel.setVerticalAlignment(JLabel.CENTER);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.add(licensePlateLabel);

        southPanel.add(labelPanel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));

        JButton yesButton = new JButton("是");
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoices.set(currentImageIndex, "是");
                String licensePlate = licensePlates.get(currentImageIndex);
                // 使用正则表达式替换车牌号
                String modifiedLicensePlate = licensePlate.replaceFirst("黄([\\u4e00-\\u9fa5][A-Z])", "$1.");
                modifiedLicensePlates.set(currentImageIndex, modifiedLicensePlate);
                updateImage();
                if (currentImageIndex < imagePaths.size() - 1) {
                    currentImageIndex++;
                    updateImage();
                }
            }
        });
        inputPanel.add(yesButton);

        JButton noButton = new JButton("否");
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoices.set(currentImageIndex, "是");
                String modifiedLicensePlate = licensePlateInput.getText();
                modifiedLicensePlates.set(currentImageIndex, modifiedLicensePlate);
                updateImage();
                if (currentImageIndex < imagePaths.size() - 1) {
                    currentImageIndex++;
                    updateImage();
                }
            }
        });
        inputPanel.add(noButton);

        licensePlateInput = new JTextField(5);
        inputPanel.add(licensePlateInput);

        JButton submitButton = new JButton("提交");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parser.setUserChoices(userChoices);
                parser.setModifiedLicensePlates(modifiedLicensePlates);
                parser.writeLicensePlatesToExcel();
                System.exit(0);
            }
        });
        inputPanel.add(submitButton);


        southPanel.add(inputPanel);

        frame.getContentPane().add(southPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);  // 让窗口在屏幕中央打开

        updateImage();
    }

    private void updateImage() {
        try {
            ImageIcon imageIcon = new ImageIcon(imagePaths.get(currentImageIndex));
            imageLabel.setIcon(imageIcon);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            pageLabel.setText((currentImageIndex + 1) + "/" + imagePaths.size());
            licensePlateLabel.setText(licensePlates.get(currentImageIndex));

        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(frame, "无法解析这个文件夹！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    public List<String> getUserChoices() {
        return userChoices;
    }

    public List<String> getModifiedLicensePlates() {
        return modifiedLicensePlates;
    }
}