package de.tot.microservices.core.batch;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.test.context.ContextConfiguration;

//@SpringApplicationConfiguration(classes = BatchServiceApplication.class)
//@WebAppConfiguration


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/context-config.xml",
								 "classpath:/job-config.xml"})
public class BatchServiceApplicationTests {

	@Autowired
	private JobLauncherTestUtils launcher;

	@Test
	public void testJob(){
		try {
			JobExecution execution = launcher.launchJob();
			Assert.assertEquals(BatchStatus.COMPLETED, execution.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testStep(){
		JobExecution execution = launcher.launchStep("batchStep");
		Assert.assertEquals(BatchStatus.COMPLETED, execution.getStatus());
	}
}
