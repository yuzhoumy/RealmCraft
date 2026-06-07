package quest.yuzhou.realmcraft.types;

public record Season(int seasonNumber, String name) {

    private static final int DAYS_PER_SEASON = 28;

    public static int getDaysInSeason() {
        return DAYS_PER_SEASON;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Season other)) return false;
        return this.seasonNumber == other.seasonNumber;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(seasonNumber);
    }

}
