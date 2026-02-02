package com.optimaNet.auth.service;


public interface KycEmailService {

    void sendKycApproved(String custName, String empName, String email, String subject);

    void sendKycRejected(String custName, String empName, String email, String subject, String reason);

    void sendKycCreated(String custName, String email, String object);

    void sendKycAssigned(String custName, String email, String object);

}
