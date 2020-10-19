package com.wilson.loyds.ordering.service.config;

import com.wilson.loyds.customer.config.domain.CustomerConfigDto;
import com.wilson.loyds.ordering.service.config.exception.CustomerConfigNotAvailableException;
import com.wilson.loyds.ordering.service.fiegn.client.CustomerConfigClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * A wrapper for Fiegn client to handle transmission errors
 */
@Component
public class CustomerConfigServiceImpl implements ICustomerConfigService {

    private CustomerConfigClient customerConfigClient;

    @Autowired
    public CustomerConfigServiceImpl(final CustomerConfigClient customerConfigClient) {
        this.customerConfigClient = customerConfigClient;
    }

    @Override
    public CustomerConfigDto getCustomerConfig(final String tenantId) {
        ResponseEntity<CustomerConfigDto> customerConfigById = this.customerConfigClient.getCustomerConfigById(tenantId);
        if (customerConfigById.getStatusCode().value() != 200) {
            throw new CustomerConfigNotAvailableException(String.format(
                    "Customer config for client %s is resulted in a non 200 Http response",
                    tenantId));
        } return customerConfigById.getBody();
    }
}
