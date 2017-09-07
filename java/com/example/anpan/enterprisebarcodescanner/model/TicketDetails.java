package com.example.anpan.enterprisebarcodescanner.model;

import java.util.Date;

/**
 * Created by anpannu on 6/11/2017.
 */

public class TicketDetails {

    public String ticketValue;
    public Date expectedDate;
    public Integer productType;

    public TicketDetails(String ticketValue, Date expectedDate, Integer productType) {
        this.ticketValue = ticketValue;
        this.expectedDate = expectedDate;
        this.productType = productType;
    }
}
