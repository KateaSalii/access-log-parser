import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private long totalTraffic;
    private OffsetDateTime minTime;
    private OffsetDateTime maxTime;

    //Переменная для хранения уникальных страниц сайта
    private Set<String> uniquePages;

    //Переменная для хранения статистики операционных систем
    private HashMap<String, Integer> osStats;

    public Statistics() {
        totalTraffic = 0;
        minTime = OffsetDateTime.MAX;
        maxTime = OffsetDateTime.MIN;

        //Инициализация коллекций
        uniquePages = new HashSet<>();
        osStats = new HashMap<>();
    }
    //Метод дл добавления записи в статистику
    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getDataSize();

        // Обновление minTime и maxTime
        if (entry.getRequestTime().isBefore(minTime)) {
            minTime = entry.getRequestTime();
        }
        if (entry.getRequestTime().isAfter(maxTime)) {
            maxTime = entry.getRequestTime();
        }

        //Добавления страницы, если код ответа 200
        if (entry.getResponseCode() == 200) {
            uniquePages.add(entry.getRequestPath());
        }

        //Получение ОС пользователя и обновления статистики ОС
        String os = entry.getUserAgent().getOs();
        osStats.put(os, osStats.getOrDefault(os, 0) + 1);
    }

    //Метод для получения списка всех уникальных страниц сайта
    public Set<String> getAllPages() {
        return uniquePages;
    }
    //Метод для расчета долей ОС
    public Map<String, Double> getOsStatistics() {
        Map<String, Double> osDistribution = new HashMap<>();
        int totalEntries = osStats.values().stream().mapToInt(Integer::intValue).sum();

        // Расчет долей для каждой ОС
        for (Map.Entry<String, Integer> entry : osStats.entrySet()) {
            double osShare = (totalEntries > 0) ? (double) entry.getValue() / totalEntries : 0;
            osDistribution.put(entry.getKey(), osShare);
        }

        return osDistribution;
    }
    //Метод для расчета среднего трафика за час
    public double getTrafficRate() {
        long hoursDifference = (maxTime.toEpochSecond() - minTime.toEpochSecond()) / 3600;
        return (hoursDifference > 0) ? (double) totalTraffic / hoursDifference : totalTraffic;
    }
}
