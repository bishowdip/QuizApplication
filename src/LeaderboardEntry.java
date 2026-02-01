/**
 * LeaderboardEntry represents a single entry in the quiz leaderboard.
 */
public class LeaderboardEntry {
    private int rank;
    private String username;
    private int bestScore;
    private double bestPercentage;

    public LeaderboardEntry(int rank, String username, int bestScore, double bestPercentage) {
        this.rank = rank;
        this.username = username;
        this.bestScore = bestScore;
        this.bestPercentage = bestPercentage;
    }

    public int getRank() {
        return rank;
    }

    public String getUsername() {
        return username;
    }

    public int getBestScore() {
        return bestScore;
    }

    public double getBestPercentage() {
        return bestPercentage;
    }

    public String getMedal() {
        return switch (rank) {
            case 1 -> "\uD83E\uDD47"; // Gold medal
            case 2 -> "\uD83E\uDD48"; // Silver medal
            case 3 -> "\uD83E\uDD49"; // Bronze medal
            default -> String.valueOf(rank);
        };
    }
}
