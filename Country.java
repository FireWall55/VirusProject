public class Country {
    public String name;
    private long population;
    // population, lists, later

    public Country(String name) {
        this.name = name;
        this.population = -1;
    }
    public String getName() {
        return this.name;
    }
    public boolean equals(Object other) {
        if (!(other instanceof Country)) return false;
        return this.getName().equals(((Country)other).getName()); 
    }
    public int hashCode() {
        return this.getName().hashCode();
    }
    public String toString() {
        return "Country name: " + this.getName();
    }
    public long getPopulation(){
        return this.population;
    }
    public void setPopulation(long pop){
        this.population = pop;
    }
}