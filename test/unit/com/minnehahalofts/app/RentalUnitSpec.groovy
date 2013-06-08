package com.minnehahalofts.app

import grails.buildtestdata.mixin.Build
import grails.plugin.spock.UnitSpec
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.joda.time.LocalDate

@TestFor(RentalUnit)
@TestMixin(GrailsUnitTestMixin)
@Build([RentalUnit, Address,Review])
class RentalUnitSpec extends UnitSpec {

    def "Enforcing Constraints"() {
        setup:
        def  rentalUnit = new RentalUnit(name: "Sydney", nickname: "Cozy Apt - Sydney", address: Address.build())
        mockForConstraintsTests(RentalUnit, [rentalUnit])

        when: "none nullable constraint ensured"
        def rentalUnitNullable = new RentalUnit()
        then:
        assert !rentalUnitNullable.validate()
        "nullable" == rentalUnitNullable.errors["name"]
        "nullable" == rentalUnitNullable.errors["nickname"]
        "nullable" == rentalUnitNullable.errors["address"]

        when: "no Blank constraint ensured"
        def rentalUnitBlank = new RentalUnit(name:' ', nickname:' ', address: Address.build())
        then:
        assert !rentalUnitBlank.validate()
        "blank" == rentalUnitBlank.errors["name"]
        "blank" == rentalUnitBlank.errors["nickname"]

        when: "unique name ensured"
        def rentalUnitUniqueName = new RentalUnit(name:'Sydney', nickname:'Charming Apt - Sydney', address: Address.build())
        then:
        assert !rentalUnitUniqueName.validate()
        "unique" == rentalUnitUniqueName.errors["name"]

        when: "size limits ensured"
        def rentalUnitSizedMin =  new RentalUnit(name:'Syd', nickname:'Char', address: Address.build())
        def rentalUnitSizedMax =  new RentalUnit(name:'x'*11, nickname:'x'*61, address: Address.build())
        then:
        assert !rentalUnitSizedMin.validate()
        assert !rentalUnitSizedMax.validate()
        "size" == rentalUnitSizedMin.errors["name"]
        "size" == rentalUnitSizedMin.errors["nickname"]
        "size" == rentalUnitSizedMax.errors["name"]
        "size" == rentalUnitSizedMax.errors["nickname"]
    }
     def "sorting reviews from newer to older"() {
         given:
         def rev1 = Review.build(dateReceived:new LocalDate(2012,10,30))
         def rev2 = Review.build(dateReceived: new LocalDate(2012,3,1))
         def rev3 = Review.build(dateReceived: new LocalDate(2012,5,29))
         mockDomain(Review,[rev1,rev2,rev3])
         def ru = RentalUnit.build()
         ru.addToReviews(rev1).addToReviews(rev2).addToReviews(rev3)

         when:
         def sortRev = ru.getReviewsSorted()

         then:
         rev1.id == sortRev[0].id
         rev2.id == sortRev[-1].id
     }

}
