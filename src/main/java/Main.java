import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URLDecoder;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        String name = "Andre Villalobos";
        int period = 1;
        String s = new java.io.File(Main.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
        String decoded = URLDecoder.decode(s, "UTF-8");
        decoded = decoded.replace(".jar", "");
        System.out.println(decoded);
        String backupName = "";
        int backupPeriod = -1;
        if(decoded.contains(",")){
            backupName = decoded.substring(0,decoded.indexOf(","));
            System.out.println(backupName);
            backupPeriod = Character.getNumericValue(decoded.charAt(decoded.length()-1));
            System.out.println(backupPeriod);
        }

//        try (FileWriter maxisabully = new FileWriter(new File("test"))) {
//            maxisabully.write("just kdding max isn't a bully :)");
//        }
        long time = System.nanoTime();
        Document mainPage = Jsoup.connect("https://www.sciencedaily.com/news/plants_animals/biology/").get();
        Elements mainLinks = mainPage.getElementsByClass("latest-head");
        HashMap<Integer, ArrayList<String>> sizeSorter = new HashMap<>();

        long timetwo = System.nanoTime();
        for(Element e: mainLinks) {

            String link = "https://www.sciencedaily.com" + e.child(0).attr("href");
            Document specificPage = Jsoup.connect(link).get();
            Element specificLink = specificPage.getElementById("text");
            ArrayList<Element> childrenList = specificLink.children(); // Holds all of the text elements
            int totalText = 0; // Number to find out which article is the shortest
            ArrayList<String> textIndex = new ArrayList<>(); // Holds all the information about the webpage
            String title = specificPage.getElementsByClass("col-sm-8 main less-padding-right hyphenate").get(0).child(3).text();
            String date = specificPage.getElementsByClass("dl-horizontal dl-custom").get(0).child(1).text(); // Finds the date
            String extraText = specificPage.getElementById("first").text(); // Gets the first paragraph
            totalText += extraText.length();
            textIndex.add(link); // Adds the link of the text to array
            textIndex.add(title); // Adds the title of the text to array
            textIndex.add(date); // Adds the date of the text to array
            textIndex.add(extraText); // Adds the first pargagraph to array, which is placed in a different body than the main text

            for (Element aChildrenList : childrenList) {
                totalText += aChildrenList.text().length();
                textIndex.add(aChildrenList.text());
            }
            if (sizeSorter.containsKey(totalText)) {
                sizeSorter.put(totalText + 1, textIndex);
            } else {
                sizeSorter.put(totalText, textIndex);
            }

        }
        System.out.println("Websites took "+(System.nanoTime() - timetwo)/1000000000 + " sec");
        for(int i =0 ; i < 2; i++){
            long timethree = System.nanoTime();

            ArrayList<String> smallestPage = sizeSorter.get(Collections.min(sizeSorter.keySet()));
            long timefour = System.nanoTime();
            if(i == 0){
                driver.get("https://docs.google.com/forms/d/e/1FAIpQLScyJT4NyPK1JlDGgZvl2Ck-N45CkFlVqktcGlLSzoZTHrd44Q/viewform");
            } else{
                ((JavascriptExecutor) driver).executeScript("window.open(arguments[0])", "https://docs.google.com/forms/d/e/1FAIpQLScyJT4NyPK1JlDGgZvl2Ck-N45CkFlVqktcGlLSzoZTHrd44Q/viewform");
                driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(i));
            }

            WebElement nameBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#mG61Hd > div > div.freebirdFormviewerViewFormContent > div.freebirdFormviewerViewItemList > div:nth-child(1) > div > div.freebirdFormviewerViewItemsTextItemWrapper > div > div.quantumWizTextinputPaperinputMainContent.exportContent > div > div.quantumWizTextinputPaperinputInputArea > input")));
            try{
                if(backupName.length() > 1){
                    nameBox.sendKeys(backupName); // NAME SLOT
                } else{
                    System.out.println("error in name-");
                    nameBox.sendKeys(name);
                }
            } catch(Error e){
                System.out.println("error in period");
                nameBox.sendKeys(name);
            }


            List<WebElement> periodNumber = driver.findElements(By.className("freebirdFormviewerViewItemsRadioOptionContainer"));
            try{
                if(backupPeriod > -1){
                    periodNumber.get(backupPeriod-1).click();
                } else{
                    periodNumber.get(period-1).click();
                }
            }catch(Error e){
                periodNumber.get(period-1).click();
            }

            WebElement titleBox = driver.findElement(By.cssSelector("#mG61Hd > div > div.freebirdFormviewerViewFormContent > div.freebirdFormviewerViewItemList > div:nth-child(3) > div > div.freebirdFormviewerViewItemsTextItemWrapper > div > div.quantumWizTextinputPaperinputMainContent.exportContent > div > div.quantumWizTextinputPaperinputInputArea > input"));
            titleBox.sendKeys(smallestPage.get(1)); // Article Title

            WebElement calendar = driver.findElement(By.cssSelector("#mG61Hd > div > div.freebirdFormviewerViewFormContent > div.freebirdFormviewerViewItemList > div:nth-child(4) > div > div.freebirdFormviewerViewItemsDateInputsContainer > div > div.quantumWizTextinputPaperinputEl.freebirdThemedInput.freebirdFormviewerViewItemsDateDateInput.modeLight > div.quantumWizTextinputPaperinputMainContent.exportContent > div > div.quantumWizTextinputPaperinputInputArea > input"));
            String date = smallestPage.get(2);
            String numberDate = date.substring(date.indexOf(' ') + 1, date.indexOf(',')).length() == 1 ? "0" + date.substring(date.indexOf(' ') + 1, date.indexOf(',')) : date.substring(date.indexOf(' ') + 1, date.indexOf(','));
//            System.out.println("" + month(date.substring(0,date.indexOf(' ')).toLowerCase()) + numberDate + Calendar.getInstance().get(Calendar.YEAR));
            calendar.sendKeys("" + month(date.substring(0,date.indexOf(' ')).toLowerCase()) + numberDate + Calendar.getInstance().get(Calendar.YEAR));

            WebElement linkBox = driver.findElement(By.cssSelector("#mG61Hd > div > div.freebirdFormviewerViewFormContent > div.freebirdFormviewerViewItemList > div:nth-child(5) > div > div.freebirdFormviewerViewItemsTextItemWrapper > div > div.quantumWizTextinputPaperinputMainContent.exportContent > div > div.quantumWizTextinputPaperinputInputArea > input"));
            linkBox.sendKeys(smallestPage.get(0));
            sizeSorter.remove(Collections.min(sizeSorter.keySet()));
//            driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(driver.getWindowHandles().size()-1));
            ((JavascriptExecutor) driver).executeScript("window.open(arguments[0])", smallestPage.get(0));
            System.out.println("Info putting took "+(System.nanoTime() - timethree)/1000000000 + " sec");

        }
        System.out.print("Took "+(System.nanoTime() - time)/1000000000 + " sec");


    }





    public static int month(String month){
        switch(month) {
            case "january":
                return 1;
            case "febuary":
                return 2;
            case "march":
                return 3;
            case "april":
                return 4;
            case "may":
                return 5;
            case "june":
                return 6;
            case "july":
                return 7;
            case "august":
                return 8;
            case "september":
                return 9;
            case "october":
                return 10;
            case "november":
                return 11;
            case "december":
                return 12;
        }
        return -1;
    }

}
