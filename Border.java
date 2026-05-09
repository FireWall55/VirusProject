public class Border {
    public Country country1;
    public Country country2;
    
    public Border(Country country1, Country country2) {
        this.country1 = country1;
        this.country2 = country2;
    }
    public String toString() {
        return "{" + this.country1.getName() + "<->" + this.country2.getName()+"}";
    }
     @Override
     public boolean equals(Object other) {
        if (!(other instanceof Border)) return false;
        Border temp = (Border)(other);
        return (this.country1.equals(temp.country1) && this.country2.equals(temp.country2)) || 
               (this.country1.equals(temp.country2) && this.country2.equals(temp.country1));
     }
        @Override
        public int hashCode() {
            return this.country1.hashCode() + this.country2.hashCode();
        }
    public Country getCountry1() {
        return this.country1;
    }
    public Country getCountry2() {
        return this.country2;
    }

}                 
