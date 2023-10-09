package com.sonata.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sonata.process.FileProcess;

public class ScheduledTaskExp {

	public static void main(String[] args) {
		try {
			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
			startScheduler(scheduler);
			//Thread.sleep(10 * 60 * 1000);
			//stopScheduler(scheduler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startScheduler(ScheduledExecutorService scheduler) {
		System.out.println("Inside startScheduler");
		try {
			// Schedule the task to run every 2 seconds with an initial delay of 0 seconds.
			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					boolean flag = FileProcess.fileProcessing();
					System.out.println("Final response is : " + flag);
				}
			}, 0, 1, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stopScheduler(ScheduledExecutorService scheduler) {
		System.out.println("Inside stopScheduler");
		// Shutdown the scheduler to stop it when done.
		scheduler.shutdown();
	}
}
