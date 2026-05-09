import com.sun.tools.javac.Main;
import java.util.*;
import java.io.*;
public class CountryGrapher {
    private final Map<Country, List<Country>> adjList;
    private final Map<String, Country> countries;
    private final Map<Country, Long> populations;
    private final Set<Border> allBorders;

     public CountryGrapher() {
        adjList = new HashMap<Country, List<Country>>();
        countries = new HashMap<String, Country>();
        allBorders = new HashSet<Border>();
        populations = new HashMap<Country, Long>();
    }
    public Set<Country> getCountrySet() {
        return adjList.keySet();
    }
    public Set<Border> getBorderSet() {
        return this.allBorders;
    }

    public boolean isConnected(Country a, Country b) {
        return false;
    }

    //public boolean shareBorder() {

    //}

    public List<Country> bfs(String x, String y){
        Country startNode = countries.get(x);
        Country targetNode = countries.get(y);

        if(!adjList.containsKey(startNode))
            return null;
        HashMap<Country, Country> pred = new HashMap<Country, Country>();
        Queue<Country> queue = new LinkedList<>();
        HashSet<Country> visited = new HashSet<>();

        queue.add(startNode);
        visited.add(startNode);
        pred.put(startNode, null);
        while(queue.size()>0){
            //go until there is nothing left
            Country curr = queue.poll();
            for(Country neighbor : adjList.get(curr)){
                if(visited.contains(neighbor)){
                    continue;
                }
                //get the neighbors of the current node
                visited.add(neighbor);
                queue.add(neighbor);
                pred.put(neighbor, curr);

                if(neighbor.equals(targetNode)){
                    System.out.println("found node");
                    //do inverse path stuff;
                    List<Country> path = new ArrayList<Country>();
                    Country start = neighbor;
                    while(!(start == null)){
                        System.out.println("building path");
                        path.add(start);
                        start = pred.get(start);
                    }
                    //path = path.reversed();
                    Collections.reverse(path);
                    return path;
                }
            }
            
        }

        ArrayList<Country> path = new ArrayList<Country>();
        return path;


    }

    public void addNode(Country node) {
        adjList.putIfAbsent(node, new ArrayList<Country>());
    }

    public void addEdge(Country node1, Country node2) {
        // Ensure both nodes exist before adding edge
        addNode(node1);
        addNode(node2);

        List<Country> list1 = adjList.get(node1);
        if (!list1.contains(node2)) {
            list1.add(node2);
        }
        List<Country> list2 = adjList.get(node2);
        if (!list2.contains(node1)) {
            list2.add(node1);
        }
    }
    public boolean isAdjacent(Country node1, Country node2) {
        return adjList.get(node1).contains(node2);
    }

    public List<Country> getNeighbors(Country node) {
        return adjList.get(node);
    }
    public Set<Border> getBorders() {
        return this.allBorders;
    }

    public void print() {
        // A -> [B,C]
        for (Map.Entry<Country, List<Country>> entry : adjList.entrySet())
            System.out.println(entry.getKey() + " -> " + entry.getValue());
    }



    public void loadData(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line ="";
            reader.readLine();
            while ((line=reader.readLine()) != null) {
                String[] temp = line.split(",");
                // for (String s : temp) {
                //     System.out.print(s + " ");
                // }
                //if !countries.contains(temp[1])
                String first = temp[1].trim(); // always exists
                Country firstCountry = null;
                if(!countries.containsKey(first)){
                    firstCountry = new Country(first);
                    countries.put(first, firstCountry);
                }else{
                    firstCountry = countries.get(first); 
                }


                if (adjList.get(firstCountry) == null) {
                        adjList.put(firstCountry, new ArrayList<Country>());
                }
                

                if (temp.length > 2) { // second one there
                    String second = temp[3].trim();
                    Country secondCountry = null;
                    if(!countries.containsKey(second)){
                        secondCountry = new Country(second);
                        countries.put(second, secondCountry);
                    }else{
                        secondCountry = countries.get(second); 
                    }
                    
                    

                    ArrayList<Country> temp2 = (ArrayList<Country>)adjList.get(firstCountry);
                    temp2.add(secondCountry);

                    Border border = new Border(firstCountry, secondCountry);
                    allBorders.add(border);
                }
                //System.out.println(temp.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadPopulationData(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line ="";
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();

            System.out.println("Loading countries: ");
            while ((line=reader.readLine()) != null) {
                String[] temp = line.split(",");
                // for (String s : temp) {
                //     System.out.print(s + " ");
                // }
                String popString = temp[temp.length - 2].trim();
                if(popString.equals("\"\"")){continue;} //there was no data 
                long popNumber = Long.parseLong(popString.substring(1,popString.length()-1));
                
                Country theCountry = countries.get(temp[0].substring(1,temp[0].length()-1));
                if(theCountry==null)
                    continue;
                System.out.println(theCountry);
                populations.put(theCountry, popNumber);
                theCountry.setPopulation(popNumber);
                System.out.println(theCountry.getPopulation());
                //System.out.println(temp.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     public Set<Country> withinRadius(String start, int radius) {
		HashSet<Country> output = new HashSet<Country>();
        Country theCountry = countries.get(start);
        System.out.println(theCountry + " -> " + adjList.get(theCountry));
        if (theCountry == null)  {
            System.out.println("Country does not exist");
            return output;
        }
        /*
        if(adjList.get(theCountry).size()==0){
            System.out.println("Country has no neighbors");
            return output;
        }*/
        //System.out.println("The country: " + theCountry);
        return withinRadiusHelper(theCountry, radius, output);
    }
    private Set<Country> withinRadiusHelper(Country start, int radius, Set<Country> holder) {
	        if (radius <= 0) {
				holder.add(start);
				return holder;
			}

	        holder.add(start); // just in case
           // System.out.println("holder: " + holder);
	        ArrayList<Country> n = (ArrayList<Country>)getNeighbors(start);
            //System.out.println("all the neighbors: " + n);
	        for (Country s : n) {
				Set<Country> temp = withinRadiusHelper(s, radius - 1, holder); // does nothing
				holder.add(s);
			}
	        return holder;
    }


    public Map<Country,List<Country>> getAdjList() {
        return this.adjList;
    }
    public Map<String, Country> getCountries() {
        return this.countries;
    }
    public Map<Country,Long> getPopulations(){
        return this.populations;
    }
    public static void main(String[] args) {
        CountryGrapher grapher = new CountryGrapher();
        grapher.loadData("CountryBorders.CSV");
        grapher.loadPopulationData("popData_updated.csv");
        //System.out.println(grapher.getAdjList() + "\n\n\n");
        //System.out.println(grapher.getCountries() + "\n\n\n");

        System.out.println(grapher.getCountries().size());
        //System.out.println((ArrayList<Country>)grapher.bfs("France", "Afghanistan"));

        System.out.println(grapher.withinRadius("Afghanistan", 2));
        System.out.println(grapher.getPopulations().get(grapher.getCountries().get("Ireland")));
        System.out.println(grapher.getPopulations().get(grapher.getCountries().get("India")));
        System.out.println(grapher.getPopulations().get(grapher.getCountries().get("Syria")));


    }



}