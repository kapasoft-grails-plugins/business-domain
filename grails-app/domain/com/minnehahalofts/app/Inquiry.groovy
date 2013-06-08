package com.minnehahalofts.app

import java.text.DateFormat
import java.text.ParseException
import org.joda.time.LocalDate
import org.joda.time.ReadablePeriod
import org.joda.time.Period

class Inquiry {
    static final boolean isCached = true
    String inqContent
    LocalDate submitDate
    String userEmail
    def timeService

    static belongsTo = [rentalUnit: RentalUnit]

    static mapping = {
        inqContent type: 'text'
    }

    static constraints = {
        userEmail size: 7..25, email: true, blank: false
        inqContent size: 6..2500, blank: false
        submitDate nullable: true, validator: {value, obj ->
            if(!value){
                obj.submitDate = obj.timeService.currentDate()
                return true
            }else{
                value.isBefore(LocalDate.now()) || value.isEqual(LocalDate.now())
            }
        }
        rentalUnit nullable: true
    }

    def propertyMissing( name ) {
        null
    }
}
