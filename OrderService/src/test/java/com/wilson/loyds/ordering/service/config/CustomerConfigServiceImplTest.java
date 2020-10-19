package com.wilson.loyds.ordering.service.config;

import com.wilson.loyds.ordering.service.config.exception.CustomerConfigNotAvailableException;
import com.wilson.loyds.ordering.service.fiegn.client.CustomerConfigClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class CustomerConfigServiceImplTest {

    @Mock
    private CustomerConfigClient customerConfigClient;
    private CustomerConfigServiceImpl classUnderTest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        this.classUnderTest = new CustomerConfigServiceImpl(customerConfigClient);
    }

    @Test
    void getCustomerConfigFail() {
        String tenantId = "TENANT_ID";
        when(this.customerConfigClient.getCustomerConfigById(tenantId)).thenReturn(ResponseEntity.badRequest().build());
        assertThrows(CustomerConfigNotAvailableException.class, () -> {
            this.classUnderTest.getCustomerConfig(tenantId);
        });
        verify(customerConfigClient).getCustomerConfigById(tenantId);
        verifyNoMoreInteractions(this.customerConfigClient);
    }

    @Test
    void getCustomerConfigSuccess() {
        String tenantId = "TENANT_ID";
        when(this.customerConfigClient.getCustomerConfigById(tenantId)).thenReturn(ResponseEntity.ok().build());
        this.classUnderTest.getCustomerConfig(tenantId);
        verify(customerConfigClient).getCustomerConfigById(tenantId);
        verifyNoMoreInteractions(this.customerConfigClient);
    }

}