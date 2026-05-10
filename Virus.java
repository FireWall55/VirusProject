public class Virus {
    
    public String name;
    private double spreadTime; // how quickly it spreads
    private double incubationTime; // how much time it takes before starting to spread
    private double fatalityTime; // how fast it unalives someone

    public Virus(String name, double spreadTime, double incubationTime, double fatalityTime) {
        this.name = name;
        this.spreadTime = spreadTime;
        this.incubationTime = incubationTime;
        this.fatalityTime = fatalityTime; 
    }

    public String getName() {
        return this.name;
    }
    public double getFatalityTime() {
        return this.fatalityTime;
    }
    public double getSpreadTime() {
        return this.spreadTime;
    }
    public double getIncubationTime() {
        return this.incubationTime;
    }
    @Override
    public String toString(){
        return "Name: " + getName() + ", FT: " + getFatalityTime() + ", ST: " + getSpreadTime() + ", IT: " + getIncubationTime();
    }

}