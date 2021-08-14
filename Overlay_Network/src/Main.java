import overlay.network.Robot;

import java.io.IOException;
import java.util.UUID;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        UUID generatedUUID = UUID.randomUUID();
        Robot myObj = new Robot(1300, generatedUUID, 0, 0);
        myObj.ping();
    }
}
