public class UserAgent {
    private final String os;
    private final String browser;

    public UserAgent(String userAgentString) {
        this.os = determineOS(userAgentString);
        this.browser = determineBrowser(userAgentString);
    }

    private String determineOS(String userAgentString) {
        if (userAgentString.contains("Windows")) {
            return "Windows";
        } else if (userAgentString.contains("Macintosh")) {
            return "macOS";
        } else if (userAgentString.contains("Linux")) {
            return "Linux";
        }
        return "Unknown OS";
    }

    private String determineBrowser(String userAgentString) {
        if (userAgentString.contains("Chrome")) {
            return "Chrome";
        } else if (userAgentString.contains("Firefox")) {
            return "Firefox";
        } else if (userAgentString.contains("Edge")) {
            return "Edge";
        } else if (userAgentString.contains("Opera")) {
            return "Opera";
        }
        return "Other";
    }

    public String getOs() {
        return os;
    }

    public String getBrowser() {
        return browser;
    }

    public boolean isBot() {
        return browser.toLowerCase().contains("bot");
    }
}
