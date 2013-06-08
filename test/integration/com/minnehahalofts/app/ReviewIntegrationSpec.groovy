package com.minnehahalofts.app

import grails.plugin.spock.IntegrationSpec
import org.springframework.dao.InvalidDataAccessApiUsageException

class ReviewIntegrationSpec extends IntegrationSpec {

    def "save and retrieve Review with all associations present"(){
        setup:
        def review = Review.build(content: 'I really liked this Apt and I am comming back').save(flush: true)
        def rentalUnit = RentalUnit.build().addToReviews(review).save(flush: true)

        when: "finding Review..."
        assert review.id == rentalUnit.reviews.first().id //ensure reference is present
        def foundReview = Review.get(review.id)

        then: "comparing name..."
        "I really liked this Apt and I am comming back" == foundReview.content
    }

    def "update Review with all associations present"(){
        setup:
        def review = Review.build(content: 'I really liked this Apt and I am comming back').save(flush: true)
        def rentalUnit = RentalUnit.build().addToReviews(review).save(flush: true)

        when: "updating..."
        review.content = 'This place is so so...it is missing hot tub'
        review.save(flush: true)

        then:
        'This place is so so...it is missing hot tub' == Review.get(review.id).content
        'This place is so so...it is missing hot tub' == rentalUnit.reviews.first().content
    }

    def "delete Review with all associations present"(){
        given:
        def review = Review.build().save(flush: true)
        def review2 = Review.build().save(flush: true)
        def rentalUnit = RentalUnit.build().save(flush: true)

        when: "delete review without associations(orphan)"
        assert Review.exists(review2.id)
        review2.delete(flush: true)

        then: "Expected: deleted"
        !Review.exists(review2.id)

        when: "delete as none orphan"
        rentalUnit.addToReviews(review).save(flush: true)
        assert 1 == rentalUnit.reviews.size()
        assert Review.exists(review.id)
        review.delete(flush: true)

        then: "has reference from RentalUnit so not permited to delete"
        thrown(InvalidDataAccessApiUsageException)
        1 == rentalUnit.reviews.size()
        1 == Review.count()
    }

    def "one-to-many from Review to RentalUnit ensured"() {
        given:
        def rentalUnit = RentalUnit.build().save(flush: true)
        def review1 = Review.build().save(flush: true)
        def review2 = Review.build().save(flush: true)

        when: "ensure multiple reviews referenced by single RentalUnit"
        rentalUnit.addToReviews(review1).addToReviews(review2).save(flush: true)

        then:
        2 == rentalUnit.reviews.size()

        when: "ensure deleting review from rental unit that contains multiple reviews"
        rentalUnit.removeFromReviews(review1).save(flush: true)

        then:
        !Review.exists(review1.id)
        assert 1 == Review.count()
        assert 1 == RentalUnit.count()
    }

    def "getImgUrl method overloaded test"() {
        given:
        def rentUnit = RentalUnit.build(name:'sydney')
        def revWithImgUrlNoRUnit = Review.build(imgUrl:'img/riga/rev1.gif')
        def revNoImgUrlNoRUnit = Review.build()
        def revWithImgUrlWithRUnit = Review.build(imgUrl:'img/sydney/rev1.gif')
        def revNoImgUrlWithRUnit = Review.build()
        rentUnit.addToReviews(revWithImgUrlWithRUnit)
        rentUnit.addToReviews(revNoImgUrlWithRUnit)

        when:
        def imgWithUrlNoRUnit = revWithImgUrlNoRUnit.imgUrl
        def imgNoUrlNoRUnit = revNoImgUrlNoRUnit.imgUrl
        def imgWithUrlWithRUnit = revWithImgUrlWithRUnit.imgUrl
        def imgNoUrlWithRUnit = revNoImgUrlWithRUnit.imgUrl

        then:
        'img/riga/rev1.gif'                     == imgWithUrlNoRUnit
        'images/minnehaha/front_min.jpg'        == imgNoUrlNoRUnit
        'img/sydney/rev1.gif'                     == imgWithUrlWithRUnit
        'images/minnehaha/sydney/front_min.jpg' == imgNoUrlWithRUnit
    }
}
