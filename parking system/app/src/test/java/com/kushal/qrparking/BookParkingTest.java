package com.kushal.qrparking;

import com.kushal.qrparking.controllers.BookingController;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class BookParkingTest implements BookingController.BookingCallBack {

    @Test
    public void bookingTest(){
        BookingController.bookParking(1,1,1,"2019-02-03 04:02 AM",this);
    }

    @Override
    public void bookingCallBack(BookingController.BookingBody responseBody) {
        assertTrue(responseBody.getStatus().equals("success"));
    }
}
