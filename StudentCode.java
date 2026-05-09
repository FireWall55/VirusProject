import java.util.*;

public class StudentCode extends Server {
    CountryGrapher grapher;
    String currSelected;

    public StudentCode() {
        currSelected = "";
        grapher = new CountryGrapher();
        grapher.loadData("CountryBorders.CSV");
        grapher.loadPopulationData("popData_updated.csv");
        long temp = 0;
        // some countries that are on the map but don't have a registered population
        grapher.getPopulations().put(grapher.getCountries().get("Holy See"), temp);
        grapher.getPopulations().put(grapher.getCountries().get("Western Sahara"), temp);

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
    public void createVirus(String data) {
        // TODO: parse data and create the virus
        sendMessageToUser("☣️ New virus created: " + data);
        System.out.println(data);
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

        /*
         * addCountryColor(country, "red");
         * sendMessageToUser("Total population of countries: " +
         * grapher.getCountries().get(country).getPopulation());
         */
        // if country doesn't exist, it will say so + pop=0. If country is island + does
        // exist, it will break
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

    }

    public void complex() {
        clearCountryColors();
        HashSet<Country> allNeighbors = (HashSet<Country>) grapher.withinRadius(currSelected, 1);

    }
}
