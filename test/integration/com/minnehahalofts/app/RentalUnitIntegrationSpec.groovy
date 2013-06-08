package com.minnehahalofts.app

import grails.plugin.spock.IntegrationSpec
//import org.springframework.dao.InvalidDataAccessApiUsageException
//import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException
//import org.springframework.orm.hibernate4.HibernateSystemException
//import org.hibernate.HibernateException

class RentalUnitIntegrationSpec extends IntegrationSpec{

    def "test setupRentalUnit helper method"(){
        when:"intializing rentalUnit intance with all associations"
        def rentalUnit = setupRentalUnit()
        def review = rentalUnit.reviews.first()
        def inquiry = rentalUnit.inquiries.first()

        then:"ensure associations"
        assert Review.all[0].id == review.id
        assert RentalUnit.all[0].id == rentalUnit.id
        assert Address.all[0].id == rentalUnit.address.id
        assert Inquiry.all[0].id == inquiry.id
    }

    def "save and retrieve RentalUnit with all associations present"(){
        given:
        def rentalUnit = setupRentalUnit()

        when: "finding RentalUnit..."
        def foundRentalUnit = RentalUnit.get(rentalUnit.id)

        then: "comparing name..."
        "Sydney" == foundRentalUnit.name
    }

    def "update RentalUnit with all associations present"(){
        setup:
        def rentalUnit = setupRentalUnit()

        when: "updating..."
        rentalUnit.name = 'Riga'
        rentalUnit.save(flush: true)

        then:
        "Riga" == RentalUnit.get(rentalUnit.id).name
    }

    def "delete RentalUnit with all associations present"(){
        setup:
        def rentalUnit = setupRentalUnit()

        when: "deleting..."
        1 == RentalUnit.count()
        1 == Review.count()
        1 == Inquiry.count()
        deleteRentalUnit(rentalUnit)

        then:
        !RentalUnit.exists(rentalUnit.id)
        0 == RentalUnit.count()
        0 == Review.count()
        0 == Inquiry.count()
    }

    /*********Address***********/

    def "cascading address ensured with all associations present"() {
        given:
        def rentalUnit = setupRentalUnit()

        when:"cascading on none orphans"
        assert 1 == Address.count()
        assert 1 == RentalUnit.count()
        deleteRentalUnit(rentalUnit)

        then:
        0 == Address.count()
        0 == RentalUnit.count()

    }

    def "one-to-one relationship between rentalUnit and Address"() {
        given:
        def addressToShare = Address.build(street: '3324 23rd Ave S').save(flush: true)

        when:'edge case for turning One-To-One into Many-to-one'
        def rentalUnit1 = RentalUnit.build(name: 'Riga', address: addressToShare).save(flush: true)
        def rentalUnit2 = RentalUnit.build(name: 'Saldus', address: addressToShare).save(flush: true)
//        rentalUnit1.address = null
        rentalUnit1.refresh()

        then:
        thrown(Exception)
    }

    /**********Review***********/

    def "cascading review ensured with all associations present"() {
        given:
        def rentalUnit = setupRentalUnit()

        when:"cascading non-orphan reviews"
        def rev2 = Review.build()
        rentalUnit.addToReviews(rev2).save(flush: true)
        assert 2 == Review.count()
        assert 1 == RentalUnit.count()
        deleteRentalUnit(rentalUnit)

        then:
        0 == Review.count()
        0 == RentalUnit.count()

        when:"cascading orphan reviews"
        rentalUnit = setupRentalUnit()
        rev2 = Review.build().save(flush: true)
        rentalUnit.addToReviews(rev2).save(flush: true)
        assert 2 == Review.count()
        assert 1 == RentalUnit.count()
        rentalUnit.removeFromReviews(rev2)

        then:
        1 == Review.count()
        1 == RentalUnit.count()
    }

    def "one-to-many relationship between RentalUnit and Review"() {
        given:
        def rentalUnit = RentalUnit.build(name: 'Riga').save(flush: true)

        when:"happy path"
        def review1 = Review.build().save(flush: true)
        def review2 = Review.build().save(flush: true)
        rentalUnit.addToReviews(review2).addToReviews(review1).save(flush: true)

        then:
        2 == rentalUnit.reviews.all.size()

        when:"adding shared review will result referenced by last rental unit"
        def review3 = Review.build().save(flush: true)
        def address1 = Address.build().save(flush: true)
        def address2 = Address.build().save(flush: true)
        def rentalUnit1 = RentalUnit.build(address: address1).addToReviews(review3).save(flush: true)
        def rentalUnit2 = RentalUnit.build(address: address2).addToReviews(review3).save(flush: true)
        rentalUnit1.refresh()

        then:
        !rentalUnit1.reviews.contains(review3)
    }


    /******Inquiry*******/

    def "cascading inquiry ensured with all associations present"() {
        given:
        def rentalUnit = setupRentalUnit()

        when:"cascading non-orphan inquiry"
        def inq2 = Inquiry.build()
        rentalUnit.addToInquiries(inq2).save(flush: true)
        assert 2 == Inquiry.count()
        assert 1 == RentalUnit.count()
        deleteRentalUnit(rentalUnit)

        then:
        0 == Inquiry.count()
        0 == RentalUnit.count()

        when:"cascading orphan inquiry"
        rentalUnit = setupRentalUnit()
        inq2 = Inquiry.build().save(flush: true)
        rentalUnit.addToInquiries(inq2).save(flush: true)
        assert 2 == Inquiry.count()
        assert 1 == RentalUnit.count()
        rentalUnit.removeFromInquiries(inq2)

        then:
        1 == Inquiry.count()
        1 == RentalUnit.count()
    }

    def "one-to-many relationship between RentalUnit and Inquiry"() {
        given:
        def rentalUnit = RentalUnit.build(name: 'Riga').save(flush: true)

        when:"happy path"
        def inquiry1 = Inquiry.build().save(flush: true)
        def inquiry2 = Inquiry.build().save(flush: true)
        rentalUnit.addToInquiries(inquiry2).addToInquiries(inquiry1).save(flush: true)

        then:
        2 == rentalUnit.inquiries.all.size()

        when:"adding shared inquiry will result referenced by last rental unit"
        def inquiry3 = Inquiry.build().save(flush: true)
        def address1 = Address.build().save(flush: true)
        def address2 = Address.build().save(flush: true)
        def rentalUnit1 = RentalUnit.build(address: address1).addToInquiries(inquiry3).save(flush: true)
        def rentalUnit2 = RentalUnit.build(address: address2).addToInquiries(inquiry3).save(flush: true)
        rentalUnit1.refresh()

        then:
        !rentalUnit1.inquiries.contains(inquiry3)
    }

    /********Company***********/
    def "ensure no cascading on Company"() {
        given:
        def ru = RentalUnit.build().save(flush: true)
        def company = Company.build().save(flush: true)

        when:
        ru.company = company
        assert 1 == Company.count()
        assert 1 == RentalUnit.count()
        assert !company.rentals
        ru.delete(flush: true)

        then:
        0 == RentalUnit.count()
        1 == Company.count()
    }

    def deleteRentalUnit(RentalUnit rU){
        rU.delete(flush: true)
    }

    def setupRentalUnit(){
        def address = Address.build().save(flush: true)
        def rentalUnit = RentalUnit.build(name:'Sydney',address: address).save(flush: true)
        def review = Review.build().save(flush: true)
        def inquiry = Inquiry.build().save(flush: true)
        rentalUnit.addToReviews(review).addToInquiries(inquiry).save(flush: true)
        rentalUnit
    }
}
