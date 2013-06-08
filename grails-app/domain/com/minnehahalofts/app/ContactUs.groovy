package com.minnehahalofts.app

import org.joda.time.LocalDate

class ContactUs {
    static final boolean isCached = false
    def timeService
    String name
    String email
    String phone
    String message
    LocalDate contactDate

    static belongsTo = [company:Company]

    static mapping = {
        message type: 'text'
    }

    static constraints = {
        name blank: false, size: 3..50
        email blank: false, size: 6..50, email: true
        phone blank: false, size: 10..16, matches: '\\d+'
        message blank: false, size: 4..2500
        contactDate nullable: true, validator: {value, obj ->
            if(!value){
                obj.contactDate = obj.timeService.currentDate()
                return true
            }else{
                value.isBefore(LocalDate.now()) || value.isEqual(LocalDate.now())
            }
        }
        company nullable: true
    }

    def propertyMissing( name ) {
        null
    }
}
