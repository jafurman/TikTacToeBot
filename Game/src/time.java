
public class time implements Runnable
    {

    private final Thread overseeingTime;

    @Override
    public void run()
    {
        final long thirtySeconds = 30000;
        try
        {
            Thread.sleep(thirtySeconds);
            overseeingTime.interrupt();
            System.out.println("Search interfered by time constraint...");
        } catch (InterruptedException e) {
            //cancelled time
        }
    }
        time(Thread parentThread) {
            this.overseeingTime = parentThread;
        }

    }