import java.time.OffsetDateTime;

public class Statistics {
    private long totalTraffic;
    private OffsetDateTime minTime;
    private OffsetDateTime maxTime;

    public Statistics() {
        totalTraffic = 0;
        minTime = OffsetDateTime.MAX;
        maxTime = OffsetDateTime.MIN;
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getDataSize();

        // Обновление minTime и maxTime
        if (entry.getRequestTime().isBefore(minTime)) {
            minTime = entry.getRequestTime();
        }
        if (entry.getRequestTime().isAfter(maxTime)) {
            maxTime = entry.getRequestTime();
        }
    }

    public double getTrafficRate() {
        long hoursDifference = (maxTime.toEpochSecond() - minTime.toEpochSecond()) / 3600;
        return (hoursDifference > 0) ? (double) totalTraffic / hoursDifference : totalTraffic;
    }
}
