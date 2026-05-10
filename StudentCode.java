import java.util.*;

public class StudentCode extends Server {
    CountryGrapher grapher;
    String currSelected;
    Virus virus;
    int days;
    HashMap<String, Long> countryDeaths;
    HashMap<String, Long> infectedPopulation; //number of people infected in a country
    ArrayList<String> infectionColors;

    public StudentCode() {
        currSelected = "";
        this.days = 0;
        grapher = new CountryGrapher();
        grapher.loadData("CountryBorders.CSV");
        grapher.loadPopulationData("popData_updated.csv");
        long temp = 0;
        this.virus = new Virus("PLACEHOLDER", 0.5, 3, 10);
        // some countries that are on the map but don't have a registered population
        grapher.getPopulations().put(grapher.getCountries().get("Holy See"), temp);
        grapher.getPopulations().put(grapher.getCountries().get("Western Sahara"), temp);

        countryDeaths = new HashMap<>();
        infectedPopulation = new HashMap<>();
        infectionColors = new ArrayList<String>();
        setupColors();

    }
    public void setupColors(){
        infectionColors.add("#faebeb");
        infectionColors.add("#f5c9c9");
        infectionColors.add("#f7b2b2");
        infectionColors.add("#ff9191");
        infectionColors.add("#f56767");
        infectionColors.add("#f74040");
        infectionColors.add("#fc1e1e");
        infectionColors.add("#d90909");
        infectionColors.add("#8c0101");
        infectionColors.add("#520101");
        infectionColors.add("#2e0000");
    }

    public static void main(String[] args) {
        Server server = new StudentCode(); // Initialize server on default port (8000).
        server.run(); // Start the server.
        server.openURL(); // Open url in browser.
    }

    @Override
    public void handleVirus(String data) {
        System.out.println("Creating virus with: " + data);
        // later you'll parse JSON here
        createVirus(data);
    }
    @Override
    public void handleNextDay() {
        System.out.println("Next day has been triggered.");
        this.days++;
    }
    public void createVirus(String data) {
        // TODO: parse data and create the virus
        data = data.substring(1, data.length() - 1);
        String[] data_vals = data.split(",");
        int spread = Integer.parseInt(data_vals[0].split(":")[1].substring(1, data_vals[0].split(":")[1].length() - 1));
        int incubation = Integer.parseInt(data_vals[1].split(":")[1].substring(1, data_vals[1].split(":")[1].length() - 1));
        int fatality = Integer.parseInt(data_vals[2].split(":")[1].substring(1, data_vals[2].split(":")[1].length() - 1));
        sendMessageToUser("☣️ New virus created: " + data);
        this.virus = new Virus("CustomVirus", spread, incubation, fatality);
    }

    @Override
    public void getInputCountries(String country1, String country2) {
        clearCountryColors();
        sendMessageToUser("The shortest path has been calculated.");
        ArrayList<Country> shortest = (ArrayList<Country>) grapher.bfs(country1, country2);
        // System.out.println(shortest);
        for (Country c : shortest) {
            addCountryColor(c.getName(), "green");
        }
        addCountryColor(country1, "red");
        addCountryColor(country2, "blue");
        setMessage("hello");
    }

    @Override
    public void getColorPath() {

    }

    @Override
    public void handleClick(String country) {
        clearCountryColors();
        currSelected = country;
        countryDeaths.putIfAbsent(country, 0L);
        infectedPopulation.putIfAbsent(country, 0L);

        if(infectedPopulation.get(country) == 0){
            infectedPopulation.put(country, 10000L);
        }

        int deaths = (int)(infectedPopulation.get(country) * virus.getFatalityTime());
        countryDeaths.put(country, countryDeaths.get(country) + deaths);

        int newInfections = (int)(infectedPopulation.get(country) * virus.getSpreadTime());
        infectedPopulation.put(country, infectedPopulation.get(country) + newInfections);
        
        double ratio = (((double)infectedPopulation.get(country))/grapher.getPopulations().get(grapher.getCountries().get(country)));//between 0 and 10
        System.out.println("infectedPop: " + infectedPopulation.get(country) + ", pop: " + 
        grapher.getPopulations().get(grapher.getCountries().get(country)) + 
        ",ratio: " + ratio + ", ratio*10: " + (ratio*10));
        if (ratio > 1) ratio = 1;
        addCountryColor(country, infectionColors.get((int)(ratio*10)));

        /*
        long total = 0;
        System.out.println("handle click");
        Set<Country> neighbors = grapher.withinRadius(country, 2);
        System.out.println("neighbors: " + neighbors);
        for (Country n : neighbors) {
            System.out.println(n);
            System.out.println(n.getPopulation());
            if (grapher.getPopulations().get(n) == null)
                System.out.println(n + " is a null population");
            total += n.getPopulation();
            addCountryColor(n.getName(), "yellow");
        }
        addCountryColor(country, "red");
        sendMessageToUser("Total population of countries: " + total);
        System.out.println(grapher.getPopulations().get(grapher.getCountries().get(country)));
        */

    }
    public void updateColors(){

    }

    

    @Override
    public int getDays() {
        return this.days;
    }
}
