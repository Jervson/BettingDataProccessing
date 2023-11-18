import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            Map<String, Match> matches = readMatchData("src/main/resources/match_data.txt");
            Map<String, Player> players = readPlayerData("src/main/resources/player_data.txt", matches);

            processPlayerActions(players, matches);

            writeResults("src/main/resources/result.txt", players);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Player> readPlayerData(String fileName, Map<String, Match> matches) throws IOException {
        Map<String, Player> players = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String playerId = values[0];
                String operation = values[1];

                Player player = players.computeIfAbsent(playerId, k -> new Player(playerId, matches));

                switch (operation) {
                    case "DEPOSIT":
                        int depositAmount = Integer.parseInt(values[3]);
                        player.deposit(depositAmount);
                        break;
                    case "BET":
                        String matchId = values[2];
                        int betAmount = Integer.parseInt(values[3]);
                        String side = values[4];
                        player.placeBet(matchId, betAmount, side);
                        break;
                    case "WITHDRAW":
                        int withdrawAmount = Integer.parseInt(values[3]);
                        player.withdraw(withdrawAmount);
                        break;
                }
            }
        }

        return players;
    }

    private static Map<String, Match> readMatchData(String fileName) throws IOException {
        Map<String, Match> matches = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String matchId = values[0];
                double rateA = Double.parseDouble(values[1]);
                double rateB = Double.parseDouble(values[2]);
                String result = values[3];

                Match match = new Match(matchId, rateA, rateB, result);
                matches.put(matchId, match);
            }
        }

        return matches;
    }

    private static void processPlayerActions(Map<String, Player> players, Map<String, Match> matches) {
        for (Player player : players.values()) {
            if (player.isLegitimate()) {
                Map<String, Integer> bets = player.getBets();

                for (Map.Entry<String, Integer> entry : bets.entrySet()) {
                    String matchId = entry.getKey();
                    int betAmount = entry.getValue();

                    Match match = matches.get(matchId);

                    if (match != null) {
                        if ("A".equals(match.getResult()) || "B".equals(match.getResult())) {

                            long betResult = player.calculateBetResults(matchId);
                            player.deposit(betResult);
                        } else if ("Draw".equals(match.getResult())) {
                            player.deposit(betAmount);
                        }
                    }
                }
            }
        }
    }

    private static void writeResults(String fileName, Map<String, Player> players) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Player player : players.values()) {
                if (player.isLegitimate()) {
                    long balance = player.getBalance();
                    double winRate = Double.parseDouble(player.calculateWinRate());
                    bw.write(player.getId() + " " + balance + " " + String.format("%.2f", winRate));
                    bw.newLine();
                }
            }

            bw.newLine();

            for (Player player : players.values()) {
                if (!player.isLegitimate()) {
                    String illegalOperationDetails = player.getIllegalOperationDetails();
                    bw.write(illegalOperationDetails);
                    bw.newLine();
                }
            }

            bw.newLine();

            long casinoBalance = calculateCasinoBalance(players);
            bw.write("Casino host balance: " + casinoBalance);
        }
    }

    private static long calculateCasinoBalance(Map<String, Player> players) {
        long casinoBalance = 0;

        for (Player player : players.values()) {
            if (player.isLegitimate()) {
                Map<String, Integer> bets = player.getBets();
                for (Map.Entry<String, Integer> entry : bets.entrySet()) {
                    String matchId = entry.getKey();
                    int betAmount = entry.getValue();

                    Match match = player.getMatches().get(matchId);

                    if (match != null && ("A".equals(match.getResult()) || "B".equals(match.getResult()))) {
                        casinoBalance += player.calculateBetResults(matchId);
                    }
                }
            }
        }

        return casinoBalance;
    }
}
