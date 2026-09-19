//Das hier sind imports für den Scraper
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//Das hier Helper
import java.io.IOException;
import java.util.ArrayList;
//Das sind alles Builder für den Chart und das Fenster.
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class Main {


    public void guiBuilder(ArrayList<Partei> parteienListe) {

        //DefaultCategoryDataset ist ein Objekt aus  jfreechart, das es mir erlaubt,
        //Datensätze zu erstellen, denen ich eine durchschnittliche Prozentzahl und
        //einen Namen zuordne.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(Partei partei: parteienListe){
            dataset.addValue(partei.returnAverage(), "Ergebnis", partei.returnName());
        }

        //Die erste Variable ist der Titel, die zweite der Name und die dritte
        //die dazu zuzuordnenden Ergebnisse, die von dem Bot kommen.
        JFreeChart diagramm = ChartFactory.createBarChart(
            "Parteienergebnisse",
            "Parteien",
            "Ergebnisse in %",
            dataset
        );
        //Keine Ahnung, wieso ich jetzt noch ein neues Panel erstellen muss, aber
        //es steht so in der Dokumentation...
        ChartPanel diagrammPanel = new ChartPanel(diagramm);


        //Ein Jframe-Fenster wird erzeugt
        JFrame fenster = new JFrame("Parteienergebnisse Bundesweit");
        fenster.setSize(500, 600);
        //Das Programm endet, wenn das Fenster geschlossen wird
        fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Das Diagrammpanel wird in das Fenster eingefügt und jenes zentriert.
        fenster.setContentPane(diagrammPanel);
        fenster.setLocationRelativeTo(null);
        fenster.setVisible(true);

    }
    public static void main(String[] args) {
        //Ab hier fängt der Scraper an.
        String url = "https://www.wahlrecht.de/umfragen/";
        ArrayList<Partei> parteienListe = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements parteiCursed = document.select(".li");
            for(Element x: parteiCursed){
                //hier kriegen wir alle Parteinamen in der linken Zeile und entfernen
                //alles Ungewünschte.

                String eintragInColumn = x.text();
                eintragInColumn = eintragInColumn.replace(".", "");
                eintragInColumn = eintragInColumn.replace("(<th class=\"li\">)", "");
                boolean isNumberWithDots = eintragInColumn.matches("\\d+");
                double totalPercentages = 0;
                int counter = 0;

                if(!isNumberWithDots && !(eintragInColumn.equalsIgnoreCase("Institut")||eintragInColumn.equalsIgnoreCase("Veröffentl")||eintragInColumn.equalsIgnoreCase("Erhebung"))) {
                    //Hier bereiten wir alles für die Erstellung unseres Objekts, also einer
                    //Partei, vor - Nur tatsächliche Parteinamen werden gespeichert...
                    String tatsaechlichePartei = eintragInColumn;
                    System.out.println("Parteiname: " + tatsaechlichePartei);
                    //... und der Durchschnitt wird berechnet.
                    Elements parteiZeile;

                    //Exception, weil der Titel im Quellcode "BSW" voll und "Sonstige" als
                    //"Sonstige Parteien" aufgelistet ist.

                    if(!(tatsaechlichePartei.equals("BSW")||tatsaechlichePartei.equals("Sonstige"))) {
                            parteiZeile = document.select("tr[title=\"" + tatsaechlichePartei + "\"]");

                    }else if(tatsaechlichePartei.equals("BSW")) {
                            parteiZeile = document.select("tr[title=\"" + "Bündnis Sahra Wagenknecht" + "\"]");
                        }else{
                            parteiZeile = document.select("tr[title=\"" + "Sonstige Parteien" + "\"]");

                    }
                    Elements tds = parteiZeile.select("td");
                    System.out.print("Prozente: ");
                    //FIXING - EINMAL WENIGER TDS DURCHGEHEN

                    Partei neuePartei = new Partei(tatsaechlichePartei, 0.0,0);

                    int erhebungen = 1;

                    //Wir gehen nur die aktuellen Studien durch und nicht die Ergebnisse
                    //des letzten Jahres, die am Ende stehen:
                        for(int i = 0; i<tds.size() -1; i++){
                            Element prozente = tds.get(i);
                            //Alle leeren Plätze werden übersprungen.
                            if(!(prozente.text().equals("") || prozente.text().equals("–"))) {
                                System.out.println("Erhebung " + erhebungen + ": ");
                                //Kommas und Prozente werden für die Berechnung ersetzt/entfernt
                                System.out.print(prozente.text().replace(" ", "")+ ", ");
                                double prozenteRemoved = Double.valueOf(prozente.text().replace(",", ".").replace("%", ""));

                                neuePartei.prozentzahlenHinzufuegen(prozente.text());

                                totalPercentages = totalPercentages + prozenteRemoved;
                                counter++;
                                erhebungen++;
                                System.out.println();
                        }

                    }
                        //Runden, um unendliche Dezimalzahlen zu vermeiden (Egal für Graphen, aber
                        //schöner in Konsole anzuschauen)
                        double average = Math.round((totalPercentages/counter)*100.0)/100.0;
                    System.out.println("Gesamterhebungen: " + (erhebungen-1));
                    System.out.println("Average: " + average + "%");
                    //Die Platzhalter von vorhin werden gefüllt und die Partei der Liste hinzufegügt
                    neuePartei.setAveragePercentage(average);
                    neuePartei.setErhebungen(erhebungen-1);
                    parteienListe.add(neuePartei);
                }
            }
            for(Partei partei : parteienListe) {
                System.out.println(partei);
                System.out.println("Einzelne Prozentzahlen: ");
                partei.printEinzelneProzentzahlen();
                System.out.println("--------------------------------");
            }
        }catch(IOException e){
            e.printStackTrace();

        }
                //Hier wird letztendlich das Fenster initialisiert
                Main mainApp = new Main();
                mainApp.guiBuilder(parteienListe);


    }
}
