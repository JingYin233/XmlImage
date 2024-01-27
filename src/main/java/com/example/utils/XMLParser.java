package com.example.utils;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class XMLParser {
    private List<String> licensePlates = new ArrayList<>();
    private List<String> creationTimes = new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>();
    private List<String> userChoices;
    private List<String> modifiedLicensePlates;
    private String date;

    public XMLParser(String date) {
        this.date = date;
    }

    public void parseXMLInFolder(String folderPath) {
        File folder = new File(folderPath);
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".xml")) {
                parseXMLFile(file.getPath());
            }
        }
    }

    private void parseXMLFile(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("ANPR");

            Path path = xmlFile.toPath();
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime fileTime = attr.lastModifiedTime();
            ZonedDateTime utcDateTime = fileTime.toInstant().atZone(ZoneId.of("UTC"));
            ZonedDateTime shanghaiDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
            String creationTime = shanghaiDateTime.toString();
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String licensePlate = eElement.getElementsByTagName("licensePlate").item(0).getTextContent();

                    if(date.equals("")){
                        licensePlates.add(licensePlate);
                        creationTimes.add(creationTime);
                    } else {
                        String formattedTime = String.valueOf(convertAndCompare(date, "yyyy年M月d日，HH:mm:ss", creationTime));
                        if (formattedTime != "null") {
                            licensePlates.add(licensePlate);
                            creationTimes.add(formattedTime);
                        }
                    }
                }
            }

            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLicensePlatesToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("车牌号");
        int rowNum = 0;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日，HH:mm:ss", Locale.CHINA);

        // 创建一个新的行，用于显示列的名称
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("识别车牌");
        headerRow.createCell(1).setCellValue("确定车牌");
        headerRow.createCell(2).setCellValue("创建时间");
        headerRow.createCell(3).setCellValue("是否匹配");
        headerRow.createCell(4).setCellValue("备注");

        for (int i = 0; i < licensePlates.size(); i++) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(licensePlates.get(i));
            row.createCell(1).setCellValue(modifiedLicensePlates.get(i));
            ZonedDateTime zdt = ZonedDateTime.parse(creationTimes.get(i), inputFormatter);
            String formattedTime = outputFormatter.format(zdt);
            row.createCell(2).setCellValue(formattedTime);
            row.createCell(3).setCellValue(userChoices.get(i));
            row.createCell(4).setCellValue("");  // 添加一个新的单元格，用于存储备注信息
        }

        try (FileOutputStream outputStream = new FileOutputStream("车牌号.xlsx")) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseJPGInFolder(String folderPath) throws IOException {
        File folder = new File(folderPath);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("License") && name.endsWith(".jpg");
            }
        };
        for (File file : folder.listFiles(filter)) {
            Path path = file.toPath();
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            if(date.equals("")){
                imagePaths.add(file.getPath());
            } else {
                String formattedTime = String.valueOf(convertAndCompare(date, "yyyy年M月d日，HH:mm:ss", attr.lastModifiedTime().toString()));
                if (formattedTime != "null") {
                    imagePaths.add(file.getPath());
                }
            }
        }
    }

    public ZonedDateTime convertAndCompare(String date, String pattern, String creationTime) {
        date = date + " +08:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日，H:m:s VV", Locale.CHINA);
        ZonedDateTime providedDate = ZonedDateTime.parse(date, formatter);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        ZonedDateTime fileDate = ZonedDateTime.parse(creationTime, inputFormatter);

        if (fileDate.isAfter(providedDate)) {
            return fileDate;
        } else {
            return null;
        }
    }


    public List<String> getImagePaths() {
        return imagePaths;
    }

    public List<String> getLicensePlates() {
        return licensePlates;
    }

    public void setUserChoices(List<String> userChoices) {
        this.userChoices = userChoices;
    }

    public void setModifiedLicensePlates(List<String> modifiedLicensePlates) {
        this.modifiedLicensePlates = modifiedLicensePlates;
    }
}
