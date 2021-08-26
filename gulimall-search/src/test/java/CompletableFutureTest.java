import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureTest {
    @Test
    public void m() {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(()->{
            System.out.println("GGC");
            return 120;
        });
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(()->{
            System.out.println("GG");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 110;
        });
        future1.acceptEither(future2,(item) ->{
            System.out.println("DD" +item);
        });
    }
}
