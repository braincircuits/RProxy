import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Init {
    public final static Init Instance = new Init();
    private Properties properties = new Properties();
    public Init()  {
        try {
            properties.load(new FileInputStream("RProxyClient.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
