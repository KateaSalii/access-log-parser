import javax.swing.*;
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
    private int validUserVisits; //Количество корректных посещений
    private int errorRequests; //Количество ошибочных запросов
    private Set<String> uniqueUserIp; // IP адреса пользователей

    private HashMap<Integer, Integer> visitsPerSecond; //Пиковая посещаемость в секунду
    private HashSet<String> refererDomains; //Список доменов
    private HashMap<String, Integer> visitsPerUser; //Посещения по ip

    public Statistics() {
        totalTraffic = 0;
        minTime = OffsetDateTime.MAX;
        maxTime = OffsetDateTime.MIN;

        //Инициализация коллекций
        uniquePages = new HashSet<>();
        nonExistentPages = new HashSet<>();
        osStats = new HashMap<>();
        browserStats = new HashMap<>();
        validUserVisits = 0;
        errorRequests = 0;
        uniqueUserIp = new HashSet<>();
        visitsPerSecond = new HashMap<>();
        refererDomains = new HashSet<>();
        visitsPerUser = new HashMap<>();
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
        //Проверка на код ответа 4хх или 5хх
        if (entry.getResponseCode() >= 400) {
            errorRequests++;
        }

        //Добавления страницы, если код ответа 200
        if (entry.getResponseCode() == 200) {
            uniquePages.add(entry.getRequestPath());
        }

        //Добавления страницы, если код ответа 404
        if (entry.getResponseCode() == 404) {
            nonExistentPages.add(entry.getRequestPath());
        }

        //Получение информации о пользователе
        String userAgent = entry.getUserAgent().getBrowser();
        String ipAddress = entry.getIp();

        //Проверка на то, является ли пользователь ботом
        if (!entry.getUserAgent().isBot()) {
            validUserVisits++;
            uniqueUserIp.add(ipAddress);
            updateVisitsPerSecond(entry.getRequestTime());
            updateVisitsPerUser(ipAddress);
        }

        //Получение ОС пользователя и обновления статистики ОС
        String os = entry.getUserAgent().getOs();
        osStats.put(os, osStats.getOrDefault(os, 0) + 1);

        //Обновление статистики браузеров
        String browser = entry.getUserAgent().getBrowser();
        browserStats.put(browser, browserStats.getOrDefault(browser, 0) + 1);

        updateRefererDomains(entry.getReferer());
    }

    //Метод для обновления пиков посещений в секунду
    private void updateVisitsPerSecond(OffsetDateTime time) {
        int second = (int) time.toEpochSecond();
        visitsPerSecond.put(second, visitsPerSecond.getOrDefault(second, 0) + 1);
    }

    //Метод для расчета пиковой посещаемости сайта в секунду
    public int getPeakVisitsPerSecond(){
        int maxVisits = 0;
        for (int visits : visitsPerSecond.values()){
            if (visits > maxVisits) {
                maxVisits = visits;
            }
        }
        return maxVisits;
    }

    //Метод для обновления рефереров
    private void updateRefererDomains(String referer) {
        if (referer != null && !referer.isEmpty()) {
            String domain = extractDomain(referer);
            if (domain != null) {
                refererDomains.add(domain);
            }
        }
    }

    //Метод для извлечения домена из URL реферера
    private String extractDomain(String url) {
        try {
            String[] parts = url.split("/+");
            return parts.length > 1 ? parts[1] : null;
        } catch (Exception e) {
            return null;
        }
    }

    //Метод для получения списка доменов-референтов
    public Set<String> getRefererDomains() {
        return refererDomains;
    }
    // Метод для обновления количества посещений для каждого пользователя
    private void updateVisitsPerUser(String ipAddress) {
        visitsPerUser.put(ipAddress, visitsPerUser.getOrDefault(ipAddress, 0) + 1);
    }

    // Метод для расчёта максимальной посещаемости одним пользователем
    public int getMaxVisitsPerUser() {
        int maxVisits = 0;
        for (int visits : visitsPerUser.values()) {
            if (visits > maxVisits) {
                maxVisits = visits;
            }
        }
        return maxVisits;
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

    //Метод для расчета среднего количества посещений за час
    public double getAverageVisitsPerHour() {
        long hoursDifference = (maxTime.toEpochSecond() - minTime.toEpochSecond()) / 3600;
        return (hoursDifference > 0) ? (double) validUserVisits / hoursDifference : validUserVisits;
    }

    //Метод для расчета среднего ошибочных запросов за час
    public double getAverageErrorRequestsPerHour() {
        long hoursDifference = (maxTime.toEpochSecond() - minTime.toEpochSecond()) / 3600;
        return (hoursDifference > 0) ? (double) errorRequests / hoursDifference : errorRequests;
    }

    //Метод для расчета средней посещаемости одним пользователем
    public double getAverageVisitsPerUser() {
        int uniqueUsers = uniqueUserIp.size();
        return (uniqueUsers > 0) ? (double) validUserVisits / uniqueUsers : validUserVisits;
    }
}
