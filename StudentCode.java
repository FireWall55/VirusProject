import java.util.*;

public class StudentCode extends Server {
    CountryGrapher grapher;
    String currSelected;
    Virus virus;
    int days;
    Boolean virusStart;
    HashMap<String, Long> countryDeaths;
    HashMap<String, Long> infectedPopulation; //number of people infected in a country
    ArrayList<String> infectionColors;
    HashSet<Country> infectedCountries;

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
        infectedCountries = new HashSet<Country>();
        virusStart = false;
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
        System.out.println("Next day has been triggered.: " + infectedCountries);
        this.days++;

        HashSet<Country> toAdd = new HashSet<>();
        
        System.out.println("\n\n\n");
        for(Country country : infectedCountries){
            String name = country.getName();

            long infected = infectedPopulation.getOrDefault(name, 0L);
            long deaths = countryDeaths.getOrDefault(name, 0L);

            countryDeaths.put(name, deaths + (long)(infected * virus.getFatalityTime()));
            infectedPopulation.put(name, infected + (long)(infected * virus.getSpreadTime()));
            //in the future need to make sure infectedPop doesn't get larger than the country's population
            long newInfected = infectedPopulation.get(name);
            Long pop = grapher.getPopulations().get(grapher.getCountries().get(name));
            if(pop != null && pop > 0) {
                double ratio = Math.min(1.0, (double) newInfected / pop);
                addCountryColor(name, infectionColors.get((int)(ratio * 10)));
            }

            List<Country> neighbors = grapher.getNeighbors(grapher.getCountries().get(name));
            if (neighbors == null) continue;
            for(Country n : neighbors){
                if(!infectedCountries.contains(n) && !toAdd.contains(n)){
                    //if the neighbor is not infected
                    if(Math.random()>.5){
                        //can change the percentage based on country stats later
                        //50% chance
                        toAdd.add(n);
                        countryDeaths.putIfAbsent(n.getName(), 0L);
                        infectedPopulation.putIfAbsent(n.getName(), 10000L);

                    }
                    System.out.println(infectedCountries);
                }
            }

        }

        infectedCountries.addAll(toAdd);
        updateColors();
            

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
        if(virusStart == false){
            clearCountryColors();
            currSelected = country;
            countryDeaths.putIfAbsent(country, 0L);
            infectedPopulation.putIfAbsent(country, 0L);
            infectedCountries.add(grapher.getCountries().get(country));

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
        }
        virusStart = true;
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
        System.out.println("UPDATING COLORS");
        for(Country c : infectedCountries){
            long newInfected = infectedPopulation.get(c.getName());
            Long pop = grapher.getPopulations().get(grapher.getCountries().get(c.getName()));
            if(pop != null && pop > 0) {
                double ratio = Math.min(1.0, (double) newInfected / pop);
                System.out.println(c.getName() + ": ratio: " + infectionColors.get((int)(ratio * 10)));
                addCountryColor(c.getName(), infectionColors.get((int)(ratio * 10)));
            }
        
        }


    }

    @Override
    public void handleReset() {
        clearCountryColors();
        days = 0;
        virusStart = false;
        currSelected = "";
        countryDeaths.clear();
        infectedPopulation.clear();
        infectedCountries.clear();
        virus = new Virus("PLACEHOLDER", 0.5, 3, 10);
        sendMessageToUser("Simulation reset to default state.");
    }

    @Override
    public String getVirusData() {
        return "{\"spreadRate\":" + virus.getSpreadTime() + ",\"incubation\":" + virus.getIncubationTime() + ",\"fatalityTime\":" + virus.getFatalityTime() + "}";
    }

    

    @Override
    public Map<String, String> getSimulationStats() {
        long totalInfected = 0;
        for (Long infected : infectedPopulation.values()) {
            totalInfected += infected;
        }

        long totalDeaths = 0;
        for (Long deaths : countryDeaths.values()) {
            totalDeaths += deaths;
        }

        Map<String, String> stats = new HashMap<>();
        stats.put("totalInfected", String.valueOf(totalInfected));
        stats.put("infectedCountries", String.valueOf(infectedCountries.size()));
        stats.put("totalDeaths", String.valueOf(totalDeaths));
        return stats;
    }

    @Override
    public int getDays() {
        return this.days;
    }
}
