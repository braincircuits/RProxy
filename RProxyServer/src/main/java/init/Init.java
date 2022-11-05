package init;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Init {
    public final static byte[] channels = new byte[]{1};
    public final static Queue<Channel> clientChannels = new ConcurrentLinkedQueue<>();
    public final static Queue<Channel> serverChannels = new ConcurrentLinkedQueue<>();
    public static final Init Instance = new Init();
    public static final ConcurrentHashMap<String, Channel> mapServer = new ConcurrentHashMap();
    public static final ConcurrentHashMap<String, Channel> mapClient = new ConcurrentHashMap();
    public static final ConcurrentHashMap<String, ByteBuf> messageClient = new ConcurrentHashMap();
    public static Channel channelM;
    public Properties properties = new Properties();

    public Init() {
        try {
            properties.load(new FileInputStream("RProxyServer.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
