package com.wilson.loyds.ordering.service.fiegn.client;

import com.wilson.loyds.customer.config.api.CustomerApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Fiegn client to retrieve customer client info
 */
@FeignClient(name = "customerConfigClient", url = "${customerConfig.url}")
public interface CustomerConfigClient extends CustomerApi {
}
