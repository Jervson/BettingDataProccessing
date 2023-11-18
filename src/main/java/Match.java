public class Match {
    private String id;
    private double rateA;
    private double rateB;
    private String result;

    public Match(String id, double rateA, double rateB, String result) {
        this.id = id;
        this.rateA = rateA;
        this.rateB = rateB;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public double getRateA() {
        return rateA;
    }

    public double getRateB() {
        return rateB;
    }

    public String getResult() {
        return result;
    }
}
