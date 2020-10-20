import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

class MyTrustAllManager implements X509TrustManager
{
    private X509Certificate[] issuers;
    public MyTrustAllManager()
    {
        this.issuers = new X509Certificate[0];
    }



    public X509Certificate[] getAcceptedIssuers()
    {
        return issuers ;
    }
    public void checkClientTrusted(X509Certificate[] chain, String authType)
    {}
    public void checkServerTrusted(X509Certificate[] chain, String authType)
    {}
}