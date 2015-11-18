package de.tot.microservices.api.batch.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import se.callista.microservices.util.ServiceUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.net.URI;
import java.security.Principal;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by bethke
 */
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class BatchApiService {

    private static final Logger LOG = LoggerFactory.getLogger(BatchApiService.class);

    @Autowired
    ServiceUtils util;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @RequestMapping("/{jobName}")
    @HystrixCommand(fallbackMethod = "defaultBatchJob")
    public ResponseEntity<String> executeJob(
            @PathVariable String jobName,
            @RequestHeader(value="Authorization") String authorizationHeader,
            Principal currentUser) {

        MDC.put("jobName", jobName);
        LOG.info("BatchAPI: User={}, Auth={}, called with jobName={}", currentUser.getName(), authorizationHeader, jobName);

        URI uri = loadBalancer.choose("batch").getUri();
        String url = uri.toString() + "/batch/" + jobName;
        LOG.debug("GetBatchJob from URL: {}", url);

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        LOG.info("GetBatchJob http-status: {}", result.getStatusCode());
        LOG.debug("GetBatchJob body: {}", result.getBody());

        return util.createResponse(result);
    }

    /**
     * Fallback method for getBatchJob()
     *
     * @param jobName
     * @return
     */
    public ResponseEntity<String> defaultBatchJob(
            @PathVariable String jobName,
            @RequestHeader(value="Authorization") String authorizationHeader,
            Principal currentUser) {

        LOG.warn("Using fallback method for batch-service. User={}, Auth={}, called with jobName={}", currentUser.getName(), authorizationHeader, jobName);
        return new ResponseEntity<String>("", HttpStatus.BAD_GATEWAY);
    }
}
