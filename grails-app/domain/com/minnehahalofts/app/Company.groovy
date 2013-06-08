package com.minnehahalofts.app

class Company {
    static final boolean isCached = false
    String fullName
    String nickname
    String email
    String phone
    Address address

    static hasMany = [rentals: RentalUnit, contactUs: ContactUs, featuredReviews: Review]

    static mapping = {
        contactUs cascade: "all-delete-orphan"
        address cascade: "all-delete-orphan"
//        rentals cascade: "all-delete-orphan"
        rentals cascade: "none"
//        featuredReviews cascade: "all-delete-orphan"
//        featuredReviews cascade: "none"
    }

    static constraints = {
        fullName blank: false, unique: true, size: 4..50
        nickname blank: false, unique: true, size: 3..15
        email blank: false, nullable: true, size: 6..50, email: true
        phone blank: false, nullable: true, size: 10..16, matches: '\\d+'
        address nullable: true
        featuredReviews nullable: true
    }

    def propertyMissing( name ) {
        null
    }
}
