package com.wilson.loyds.ordering.service.config;

import com.wilson.loyds.customer.config.domain.CustomerConfigDto;

public interface ICustomerConfigService {

    CustomerConfigDto getCustomerConfig(String tenantId);

}
