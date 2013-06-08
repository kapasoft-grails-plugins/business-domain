package com.minnehahalofts.app

class Address {
    static final boolean isCached = false
    String street
    String city
    String state
    String country
    String zip

    static belongsTo = [rentalUnit: RentalUnit, company: Company]

    static constraints = {
        street blank: false, size: 6..50
        city blank: false, size: 3..20
        state blank: false, size: 2..2
        country blank: false, size: 2..5
        zip blank: false, size: 5..10, validator:{zip ->
            (zip ==~ /^(\d{5}-\d{4})|(\d{5})$/) ? true : false
        }
        rentalUnit nullable: true
        company nullable: true
    }

    def propertyMissing( name ) {
        null
    }
}