package local.utils;

import java.io.Serializable;
import java.util.UUID;

public class messageWrappers
{
    public static class pingMessage implements Serializable
    {
        UUID idNum;
        int seqNum;
        String ping = "ping";
        public pingMessage(UUID idNum, int seqNum)
        {
            super();
            this.idNum = idNum;
            this.seqNum = seqNum;
        }

        public UUID getUUID()
        {
            return this.idNum;
        }
        public int getSeqNum()
        {
            return this.seqNum;
        }
    }
}
