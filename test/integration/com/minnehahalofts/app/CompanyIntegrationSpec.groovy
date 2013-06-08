package com.minnehahalofts.app

import grails.plugin.spock.IntegrationSpec

class CompanyIntegrationSpec extends IntegrationSpec {


    def "test setupCompany helper method"(){
        when:"intializing Company intance with all associations present"
        def company = setupCompany('test')
        def review = company.featuredReviews.first()
        def rental = company.rentals.first()
        def address = company.address
        def contactUs = company.contactUs.first()

        then:"ensure associations"
        'test' == company.nickname
        1 == Company.count()
        Review.all[0].id == review.id
        RentalUnit.all[0].id == rental.id
        Address.all[0].id == address.id
        ContactUs.all[0].id == contactUs.id
    }

    def "save and retrieve RentalUnit with all associations present"(){
        given:
        def company = setupCompany('comp1')

        when: "finding Company..."
        def foundCompany = Company.get(company.id)

        then: "comparing name..."
        1 == Company.count()
        'comp1' == foundCompany.nickname
    }

    def "update Company with all associations present"(){
        setup:
        def company = setupCompany()

        when: "updating..."
        company.nickname = 'Riga'
        company.save(flush: true)

        then:
        "Riga" == Company.get(company.id).nickname
    }

    def "delete Company with all associations present"(){
        setup:
        def company = setupCompany()

        when: "deleting..."
        1 == Review.count()
        1 == ContactUs.count()
        1 == RentalUnit.count()
        2 == Address.count()//one for company another for rentalUnit
        deleteCompany(company)

        then:
        !Company.exists(company.id)
        0 == ContactUs.count()
        1 == RentalUnit.count() //rental unit no cascaded on delete
        1 == Review.count()
        1 == Address.count() //rentalUnit address
    }

    /*********Address***********/

    def "cascading address ensured with all associations present"() {
        given:
        def company = setupCompany()

        when:"cascading on none orphans"
        assert 2 == Address.count()//one for company another for rentalUnit
        assert 1 == Company.count()
        deleteCompany(company)

        then:
        1 == Address.count()//rental unit address remains
        0 == Company.count()
    }

    def "one-to-one relationship between Company and Address"() {
        given:
        def addressToShare = Address.build(street: '3324 23rd Ave S').save(flush: true)

        when:'edge case for turning One-To-One into Many-to-one should fail for bidirection relationship'
        def company1 = Company.build(nickname: 'lofts', address: addressToShare).save(flush: true)
        def company2 = Company.build(nickname: 'rentals', address: addressToShare).save(flush: true)
        company1.refresh()

        then:
        thrown(Exception)
    }

    /**********Review***********/

    def "non-cascading featuredReview ensured with all associations present"() {
        given:
        def company = setupCompany()
        def review = Review.build().save(flush: true)
        company.addToFeaturedReviews(review).save(flush: true)

        when:"non-cascading reviews"
        assert company.rentals.first().reviews.first().id != review.id
        assert 2 == Review.count()
        assert 1 == RentalUnit.count()
        assert 1 == Company.count()
        assert 2 == company.featuredReviews.size()
        deleteCompany(company)

        then:
        1 == RentalUnit.count()
        2 == Review.count()
        0 == Company.count()
    }
    def "non-cascading orphan reviews"() {
        given:
        given:
        def company = setupCompany()
        def review = Review.build().save(flush: true)
        company.addToFeaturedReviews(review).save(flush: true)

        when:
        assert 2 == Review.count()
        assert 1 == Company.count()
        company.removeFromFeaturedReviews(review)
        company.removeFromFeaturedReviews(company.rentals.first().reviews.first())

        then:
        2  == Review.count()
        1 == Company.count()
    }

    def "one-to-many unidirectional relationship between Company and Review"() {
        given:
        def sharedReview = Review.build()
        def company = Company.build()
        def company2 = Company.build()


        when:
        company.addToFeaturedReviews(sharedReview).save(flush: true)
        company2.addToFeaturedReviews(sharedReview).save(flush: true)
        1 == company.featuredReviews.size()
        1 == company2.featuredReviews.size()
        company.refresh()

        then:/*this should fail and it fails for bidirectional relationship. Currently, looking in why it is not for unidirectional */
        company2.featuredReviews.first().id == company.featuredReviews.first().id

        when:/*happy path one-to-many*/
        def rev = Review.build()
        assert 1 == company2.featuredReviews.size()
        company2.addToFeaturedReviews(rev).save(flush: true)

        then:
        2 == company2.featuredReviews.size()
    }

    /*******RentalUnit***********/
    def "ensure no-cascading on rental unit"() {
        given:
        def company = setupCompany()

        when:
        assert 1 == RentalUnit.count()
        assert 1 == Company.count()
        deleteCompany(company)

        then:
        0 == Company.count()
        1 == RentalUnit.count()
    }

    def "ensure no-cascading on orphan rental unit"() {
        given:
        def company = setupCompany()

        when:
        assert 1 == RentalUnit.count()
        assert 1 == Company.count()
        company.removeFromRentals(company.rentals.first())

        then:
        1 == RentalUnit.count()
    }

    def "one-to-many bidirectional relationship between company and rental unit ensured"() {
        given:
        def company = setupCompany()
        def ru = RentalUnit.build()

        when:/*happy path */
        assert 1 == company.rentals.size()
        company.addToRentals(ru)

        then:
        2 == company.rentals.size()

        when:/*adding shared rental unit should fail*/
        def company2 = setupCompany('minehah2')
        assert company.rentals.contains(ru)
        company2.addToRentals(ru).save(flush: true)
        company.refresh()

        then:
        !company.rentals.contains(ru)
    }

    /********ContactUs*********/
    def "ensure cascading on Connection"() {
        given:
        def company = setupCompany()

        when:
        assert 1 == ContactUs.count()
        assert 1 == Company.count()
        deleteCompany(company)

        then:
        0 == ContactUs.count()
        0 == Company.count()
    }

    def "ensure cascading on orphan ContactUs"() {
        given:
        def company = setupCompany()

        when:
        assert 1 == ContactUs.count()
        assert 1 == Company.count()
        company.removeFromContactUs(company.contactUs.first())

        then:
        0 == ContactUs.count()
        1 == Company.count()
    }

    def "one-to-many bidirectional relationship between rental unit and ContactUs ensured"() {
        given:
        def company = setupCompany()
        def contact = ContactUs.build()

        when:
        assert 1 == company.contactUs.size()
        company.addToContactUs(contact)

        then:
        2 == company.contactUs.size()

        when:/*sharing ContactUs should fail*/
        def company2 = setupCompany('nikki')
        def sharedContactUs = ContactUs.build()
        company.addToContactUs(sharedContactUs).save(flush: true)
        assert company.contactUs.contains(sharedContactUs)
        company2.addToContactUs(sharedContactUs).save(flush: true)
        company.refresh()

        then:
        !company.contactUs.contains(sharedContactUs)
    }

    /**********Helper Methods********/
    def deleteCompany(Company company){
        company.rentals.each{
            company.removeFromRentals(it)
        }
        company.delete(flush: true)
    }

    def setupCompany(String nickname = 'minnehaha'){
        def addressForCompany = Address.build().save(flush: true)
        def addressForRental = Address.build(street:"happy blvd").save(flush: true)
        def company = Company.build(nickname: nickname, address: addressForCompany)
        def rental = RentalUnit.build(address: addressForRental)
        def review = Review.build().save(flush: true)
        def contactUs = ContactUs.build().save(flush: true)

        rental.addToReviews(review).save(flush: true)
        company.addToFeaturedReviews(review)
        company.addToRentals(rental)
        company.addToContactUs(contactUs).save(flush: true)

        return company
    }



}