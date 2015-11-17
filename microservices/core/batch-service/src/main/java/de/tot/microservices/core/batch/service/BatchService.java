package de.tot.microservices.core.batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by magnus on 04/03/15.
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class BatchService {

    private static final Logger LOG = LoggerFactory.getLogger(BatchService.class);

    String[] springConfig  = {"META-INF/spring/job-config.xml"};

    public BatchService() {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
            JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
            Job job = (Job) context.getBean("batchJob");

            FlatFileItemReader reader = (FlatFileItemReader) context.getBean("itemReader");
            reader.setResource(getTestInput());

            try {
                JobExecution execution = jobLauncher.run(job,  new JobParameters());

                System.out.println("Exit status: " + execution.getStatus());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Resource getTestInput() {
        return new UrlResource(this.getClass().getResource("/META-INF/sampleData/studentData.csv"));
    }

    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param batchId
     * @return
     */
    @RequestMapping("/batch/{batchId}")
    public void getBatch(@PathVariable int batchId) {
        LOG.info("/batch called:" + batchId);

//        return new Product(productId, "name", 123);
	return;
    }
}
