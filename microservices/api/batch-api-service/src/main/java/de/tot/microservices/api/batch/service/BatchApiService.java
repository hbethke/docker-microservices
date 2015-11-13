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

    @RequestMapping("/{batchId}")
    @HystrixCommand(fallbackMethod = "defaultBatch")
    public ResponseEntity<String> runBatch (
            @PathVariable int batchId,
            @RequestHeader(value="Authorization") String authorizationHeader,
            Principal currentUser) {

        MDC.put("batchId", batchId);
        LOG.info("BatchApi: User={}, Auth={}, called with batchId={}", currentUser.getName(), authorizationHeader, batchId);

        URI uri = loadBalancer.choose("batch").getUri();
        String url = uri.toString() + "/batch/" + batchId;
        LOG.debug("Run batch from URL: {}", url);

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        LOG.info("runBatch http-status: {}", result.getStatusCode());
        LOG.debug("runBatch body: {}", result.getBody());

        return util.createResponse(result);
    }

    /**
     * Fallback method for runBatch()
     *
     * @param batchId
     * @return
     */
    public ResponseEntity<String> defaultBatch(
            @PathVariable int batchId,
            @RequestHeader(value="Authorization") String authorizationHeader,
            Principal currentUser) {

        LOG.warn("Using fallback method for batch-service. User={}, Auth={}, called with batchId={}", currentUser.getName(), authorizationHeader, batchId);
        return new ResponseEntity<String>("", HttpStatus.BAD_GATEWAY);
    }
}
