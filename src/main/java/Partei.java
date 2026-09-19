import java.util.ArrayList;


public class Partei {

    private String name;
    private double averagePercentage;
    private int Erhebungen;
    private ArrayList<String> einzelneProzentzahlen;

    public Partei(String name, double averagePercentage, int Erhebungen){
        this.name = name;
        this.averagePercentage = averagePercentage;
        this.Erhebungen = Erhebungen;
        this.einzelneProzentzahlen = new ArrayList<>();
    }
    public void prozentzahlenHinzufuegen(String prozentzahl){
        einzelneProzentzahlen.add(prozentzahl);
    }
    public void setAveragePercentage(double averagePercentage){
        this.averagePercentage = averagePercentage;
    }

    public void setErhebungen(int erhebungen){
        this.Erhebungen = erhebungen;
    }
    public void printEinzelneProzentzahlen(){
        for(String prozent:einzelneProzentzahlen){
            System.out.println(prozent);
        }
    }
    public String returnName(){
        return this.name;
    }
    public double returnAverage(){
        return this.averagePercentage;
    }
    public int returnErhebungen(){
        return this.Erhebungen;
    }
    @Override
    public String toString(){
        return "Partei: " + this.name + ", durchschnittliche Prozente: " + this.averagePercentage + ", Erhebungen: " + this.Erhebungen + ".";
    }

}
