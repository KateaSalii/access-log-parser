import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private long totalTraffic;
    private OffsetDateTime minTime;
    private OffsetDateTime maxTime;

    //Переменные для хранения страниц сайта
    private Set<String> uniquePages; //Переменная для существующих страниц (код 200)
    private Set<String> nonExistentPages; //Переменная для несуществующих страниц (код 404)

    //Переменная для хранения статистики операционных систем
    private HashMap<String, Integer> osStats; //Статистика по ОС
    private HashMap<String, Integer> browserStats; //Статистика по браузерам

    public Statistics() {
        totalTraffic = 0;
        minTime = OffsetDateTime.MAX;
        maxTime = OffsetDateTime.MIN;

        //Инициализация коллекций
        uniquePages = new HashSet<>();
        nonExistentPages = new HashSet<>();
        osStats = new HashMap<>();
        browserStats = new HashMap<>();
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

        //Добавления страницы, если код ответа 404
        if (entry.getResponseCode() == 404) {
            nonExistentPages.add(entry.getRequestPath());
        }

        //Получение ОС пользователя и обновления статистики ОС
        String os = entry.getUserAgent().getOs();
        osStats.put(os, osStats.getOrDefault(os, 0) + 1);

        //Обновление статистики браузеров
        String browser = entry.getUserAgent().getBrowser();
        browserStats.put(browser, browserStats.getOrDefault(browser, 0) + 1);
    }

    //Метод для получения списка всех уникальных страниц сайта c кодом 200
    public Set<String> getAllPages() {
        return uniquePages;
    }

    //Метод для получения списка всех уникальных страниц сайта c кодом 404
    public Set<String> getNonExistentPages() {
        return nonExistentPages;
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

    //Метод для расчета долей браузеров
    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> browserDistribution = new HashMap<>();
        int totalEntries = browserStats.values().stream().mapToInt(Integer::intValue).sum();

        // Расчет долей для каждого браузера
        for (Map.Entry<String, Integer> entry : browserStats.entrySet()) {
            double browserShare = (totalEntries > 0) ? (double) entry.getValue() / totalEntries : 0;
            browserDistribution.put(entry.getKey(), browserShare);
        }

        return browserDistribution;
    }
    //Метод для расчета среднего трафика за час
    public double getTrafficRate() {
        long hoursDifference = (maxTime.toEpochSecond() - minTime.toEpochSecond()) / 3600;
        return (hoursDifference > 0) ? (double) totalTraffic / hoursDifference : totalTraffic;
    }
}
