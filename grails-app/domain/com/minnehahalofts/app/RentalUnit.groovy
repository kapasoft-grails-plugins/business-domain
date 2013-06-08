package com.minnehahalofts.app

class RentalUnit {
    static final boolean isCached = false
    String name
    String nickname
    Address address
    static hasMany = [reviews:Review,inquiries:Inquiry ]

    static belongsTo = [company:Company]

    static mapping = {
        reviews cascade: "all-delete-orphan"
        inquiries cascade: "all-delete-orphan"
        address cascade: "all-delete-orphan"
    }

    static constraints = {
        name blank: false, unique: true, size: 4..10
        nickname blank: false, size: 5..60
        company nullable: true
    }

    List<Review> getReviewsSorted(){
        def sortRev = this.reviews.sort{Review a, Review b ->
            b.dateReceived <=> a.dateReceived
        }
        sortRev
    }
    def propertyMissing( name ) {
        null
    }
}
