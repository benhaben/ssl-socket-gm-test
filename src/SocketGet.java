import java.net.*;
import java.io.*;
import java.security.*;

import javax.net.*;
import javax.net.ssl.*;

public class SocketGet
{
    public static void main(String[] args)
    {
        SocketFactory fact = null;
        SSLSocket socket = null;

        String addr = "ebssec.boc.cn";
        int port = 443;
        String uri = "/";

        try
        {
            if(args.length > 0)
            {
                addr = args[0];
                port = Integer.parseInt(args[1]);
                uri = args[2];
            }

            System.out.println("\r\naddr="+addr);
            System.out.println("port="+port);
            System.out.println("uri="+uri);

            // 加载国密提供者
            Security.insertProviderAt(new cn.gmssl.jce.provider.GMJCE(), 1);
            Security.insertProviderAt(new cn.gmssl.jsse.provider.GMJSSE(), 2);

            fact = createSocketFactory(null, null);
            socket = (SSLSocket)fact.createSocket();
            socket.setTcpNoDelay(true);

            System.out.println("\r\nGM SSL connecting...");
            socket.connect(new InetSocketAddress(addr, port), 5000);
            socket.setTcpNoDelay(true);
            socket.startHandshake();

            System.out.println("Connected!\n");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            OutputStream out = socket.getOutputStream();

            String s = "GET " + uri + " HTTP/1.1\r\n";
            s+= "Accept: */*\r\n";
            s+= "User-Agent: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)\r\n";
            s+= "Host: " + addr + (port == 443 ? "" : ":"+port) + "\r\n";
            s+= "Connection: Close\r\n";
            s+= "\r\n";
            out.write(s.getBytes());
            out.flush();

            // 读取HTTP头
            while(true)
            {
                byte[] lineBuffer = ReadLine.read(in);
                if ( lineBuffer == null || lineBuffer.length == 0)
                {
                    System.out.println();
                    break;
                }
                String line = new String(lineBuffer);
                System.out.println(line);
            }
            // 读取HTTP内容
            {
                byte[] buf = new byte[1024];
                while(true)
                {
                    int len = in.read(buf);
                    if(len == -1)
                    {
                        break;
                    }
                    System.out.println(new String(buf, 0, len));
                }
            }
            in.close();
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch(Exception e)
            {}
        }
    }
    private static SSLSocketFactory createSocketFactory(KeyStore kepair, char[] pwd) throws Exception
    {
        X509TrustManager[] trust = { new MyTrustAllManager() };
        KeyManager[] kms = null;
        if (kepair != null)
        {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(kepair, pwd);
            kms = kmf.getKeyManagers();
        }
        // 使用国密SSL
        String protocol = cn.gmssl.jsse.provider.GMJSSE.GMSSLv11;
        String provider = cn.gmssl.jsse.provider.GMJSSE.NAME;
        SSLContext ctx = SSLContext.getInstance(protocol, provider);
        java.security.SecureRandom secureRandom = new java.security.SecureRandom();
        ctx.init(kms, trust, secureRandom);
        SSLSocketFactory factory = ctx.getSocketFactory();
        return factory;
    }
}