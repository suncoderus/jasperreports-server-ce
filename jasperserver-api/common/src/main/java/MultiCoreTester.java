/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import java.lang.management.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class MultiCoreTester {
  private static final int THREADS = 8;
  private static final int CYCLES = 1000 * 1000 * 1000;
  private static CountDownLatch ct = new CountDownLatch(THREADS);
  private static AtomicLong total = new AtomicLong();

  public static void main(String[] args)
      throws InterruptedException {
    System.out.println("Working... ");
    long elapsedTime = System.nanoTime();
    for (int i = 0; i < THREADS; i++) {
      Thread thread = new Thread() {
        public void run() {
          total.addAndGet(measureThreadCpuTime());
          ct.countDown();
        }
      };
      thread.start();
    }
    ct.await();
    elapsedTime = System.nanoTime() - elapsedTime;
    System.out.println("Total elapsed time " + elapsedTime);
    System.out.println("Total thread CPU time " + total.get());
    double factor = total.get();
    factor /= elapsedTime;
    System.out.printf("Factor: %.2f%n", factor);
    int cpuReported = Runtime.getRuntime().availableProcessors();
    System.out.println("\nResults:");
    System.out.printf("Java report on CPU count : %d%n", cpuReported);
    System.out.printf("Calculated CPU count : %d%n", Math.round(factor));
  }

  private static long measureThreadCpuTime() {
    ThreadMXBean tm = ManagementFactory.getThreadMXBean();
    long cpuTime = tm.getCurrentThreadCpuTime();
    long total=0;
    for (int i = 0; i < CYCLES; i++) {
      // keep ourselves busy for a while ...
      // note: we had to add some "work" into the loop or Java 6
      // optimizes it away.  Thanks to Daniel Einspanjer for
      // pointing that out.
      total += i;
      total *= 10;
    }
    cpuTime = tm.getCurrentThreadCpuTime() - cpuTime;
    System.out.println(total + " ... " + Thread.currentThread() +
        ": cpuTime = " + cpuTime);
    return cpuTime;
  }
}