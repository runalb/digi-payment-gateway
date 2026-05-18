package com.runalb.ondemand_service.offering.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogServiceOfferingService {

    // TODO: Implement this method
    // get active and verified offerings for a catalog service
    public void listActiveAndVerifiedOfferingsForService(Long catalogServiceId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Not implemented");
    }
}
