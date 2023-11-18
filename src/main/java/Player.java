import java.util.HashMap;
import java.util.Map;


public class Player {
    private String id;
    private long balance;
    private int totalBets;
    private int wonBets;
    private boolean legitimate;
    private String illegalOperationDetails;
    private Map<String, Integer> bets;
    private Map<String, Match> matches;

    public Player(String id, Map<String, Match> matches) {
        this.id = id;
        this.balance = 0;
        this.totalBets = 0;
        this.wonBets = 0;
        this.legitimate = true;
        this.illegalOperationDetails = null;
        this.bets = new HashMap<>();
        this.matches = matches;
    }

    public String getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isLegitimate() {
        return legitimate;
    }

    public String calculateWinRate() {
        if (totalBets == 0) {
            return "0.00";
        }
        double winRate = (double) wonBets / totalBets;
        return String.format("%.2f", winRate);
    }

    public String getIllegalOperationDetails() {
        return illegalOperationDetails;
    }

    public void deposit(long amount) {
        balance += amount;
    }

    public void withdraw(long amount) {
        if (amount <= balance) {
            balance -= amount;
        } else {
            legitimate = false;
            illegalOperationDetails = id + " WITHDRAW null " + amount + " null";
        }
    }

    public void placeBet(String matchId, int amount, String side) {
        if (amount > balance) {
            legitimate = false;
            illegalOperationDetails = id + " BET " + matchId + " " + amount + " " + side;
            return;
        }

        totalBets++;
        bets.put(matchId, amount);

        Match match = matches.get(matchId);

        if (match != null) {
            if ("A".equals(side) && "A".equals(match.getResult())) {
                wonBets++;
                balance += (int) (amount * match.getRateA());
            } else if ("B".equals(side) && "B".equals(match.getResult())) {
                wonBets++;
                balance += (int) (amount * match.getRateB());
            } else if ("Draw".equals(match.getResult())) {
                balance += amount;
            }
        }
    }

    public long calculateBetResults(String matchId) {
        long netResult = 0;

        int betAmount = bets.get(matchId);
        Match match = matches.get(matchId);

        if ("A".equals(match.getResult())) {
            netResult += (int) (betAmount * match.getRateA());
        } else if ("B".equals(match.getResult())) {
            netResult += (int) (betAmount * match.getRateB());
        }

        return netResult;
    }
    public Map<String, Integer> getBets() {
        return bets;
    }

    public Map<String, Match> getMatches() {
        return matches;
    }
    public void addMatch(String matchId, Match match) {
        matches.put(matchId, match);
    }
}
