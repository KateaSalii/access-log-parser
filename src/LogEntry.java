import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private final String ipAddress;
    private final OffsetDateTime requestTime;
    private final HttpMethod method;
    private final String requestPath;
    private final int responseCode;
    private final int dataSize;
    private final String referer;
    private final UserAgent userAgent;

    public LogEntry(String logLine) {
        String regex = "(\\S+) - - \\[(.+?)\\] \"(\\w+) (.+?) HTTP/\\S+\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*?)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(logLine);

        if (matcher.find()) {
            this.ipAddress = matcher.group(1);
            this.requestTime = parseRequestTime(matcher.group(2));
            this.method = HttpMethod.valueOf(matcher.group(3));
            this.requestPath = matcher.group(4);
            this.responseCode = Integer.parseInt(matcher.group(5));
            this.dataSize = Integer.parseInt(matcher.group(6));
            this.referer = matcher.group(7);
            this.userAgent = new UserAgent(matcher.group(8));
        } else {
            throw new IllegalArgumentException("Invalid log line format: " + logLine);
        }
    }

    private OffsetDateTime parseRequestTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        return OffsetDateTime.parse(time, formatter);
    }

    public String getIp() {
        return ipAddress;
    }

    public OffsetDateTime getRequestTime() {
        return requestTime;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getDataSize() {
        return dataSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    enum HttpMethod {
        GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH
    }
}
