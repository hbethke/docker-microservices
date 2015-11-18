package de.tot.microservices.core.batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private static ApplicationContext context = null;
    private static JobLauncher jobLauncher = null;

    public BatchService() {
        try {
            LOG.info("Starting Batch Service");
            this.context = new ClassPathXmlApplicationContext(springConfig);
            this.jobLauncher = (JobLauncher) context.getBean("jobLauncher");
            LOG.info("Done");
        } catch (Exception e) {
            LOG.error("Error on creating BatchService:" + e.getMessage(), e);
        }
    }

    private Resource getTestInput() {
        return new UrlResource(this.getClass().getResource("/META-INF/sampleData/studentData.csv"));
    }

    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param jobName
     * @return
     */
    @RequestMapping("/batch/{jobName}")
    public String getBatch(@PathVariable String jobName) {
        LOG.info("/batch called:" + jobName);

        try {
            Job job = (Job) context.getBean("batchJob");

            FlatFileItemReader reader = (FlatFileItemReader) context.getBean("itemReader");
            reader.setResource(getTestInput());

            JobExecution execution = jobLauncher.run(job,  new JobParameters());

            LOG.info("Exit status: " + execution.getStatus());

            return "Job exit status: " + execution.getExitStatus();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return "Job error: " + e.getMessage();
        }
    }
}
