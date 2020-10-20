import java.io.*;
import java.net.SocketException;

class ReadLine
{
    public static final byte[] CRLF = {'\r', '\n'};
    public static final byte CR = '\r';
    public static final byte LF = '\n';
    private static final int LINE_MAX_SIZE = 16384;
    public static byte[] read(DataInputStream in) throws IOException, SocketException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream s = new DataOutputStream(baos);
        boolean previousIsCR = false;
        int len = 0;
        byte b = 0;
        try
        {
            b = in.readByte();
            len ++;
        }
        catch(EOFException e)
        {
            return new byte[0];
        }
        while(true)
        {
            if(b == LF)
            {
                if(previousIsCR)
                {
                    s.flush();
                    byte[] rs = baos.toByteArray();
                    s.close();
                    return rs;
                }
                else
                {
                    s.flush();
                    byte[] rs = baos.toByteArray();
                    s.close();
                    return rs;
                }
            }
            else if(b == CR)
            {
                if(previousIsCR)
                {
                    s.writeByte(CR);
                }
                previousIsCR = true;
            }
            else
            {
                if(previousIsCR)
                {
                    s.writeByte(CR);
                }
                previousIsCR = false;
                s.write(b);
            }
            if(len > LINE_MAX_SIZE)
            {
                s.close();
                throw new IOException("Reach line size limit");
            }
            try
            {
                b = in.readByte();
                len ++;
            }
            catch(EOFException e)
            {
                s.flush();
                byte[] rs = baos.toByteArray();
                s.close();
                return rs;
            }
        }
    }
}