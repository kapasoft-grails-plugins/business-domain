package com.minnehahalofts.app

import grails.buildtestdata.mixin.Build
import grails.plugin.spock.UnitSpec
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(Address)
@TestMixin(GrailsUnitTestMixin)
@Build(Address)
class AddressUnitSpec extends UnitSpec{

    def "Enforcing Constraints"() {
        setup:
        def  address = new Address(street: "2816 32nd Ave S", city: "Minneapolis", state: "MN", country:"US", zip: "55407")
        mockForConstraintsTests(Address, [address])

        when: "none nullable constraint ensured"
        def addressNullable = new Address()
        then:
        assert !addressNullable.validate()
        "nullable" == addressNullable.errors["street"]
        "nullable" == addressNullable.errors["city"]
        "nullable" == addressNullable.errors["state"]
        "nullable" == addressNullable.errors["country"]
        "nullable" == addressNullable.errors["zip"]

        when: "no Blank constraint ensured"
        def addressBlank = new Address(street:'', city:'', state: '', country: '', zip: '')
        then:
        assert !addressBlank.validate()
        "blank" == addressBlank.errors["street"]
        "blank" == addressBlank.errors["city"]
        "blank" == addressBlank.errors["state"]
        "blank" == addressBlank.errors["country"]
        "blank" == addressBlank.errors["zip"]

        when: "size limits ensured"
        def addressSizeMin = new Address(street: 'x'*5, city: 'xx', state: 'x', country:'x', zip: 'x'*4)
        def addressSizeMax = new Address(street: 'x'*51, city: 'x'*21, state: 'x'*3, country:'x'*6, zip: 'x'*11)
        then:
        assert !addressSizeMin.validate()
        assert !addressSizeMax.validate()
        "size" == addressSizeMin.errors["street"]
        "size" == addressSizeMin.errors["city"]
        "size" == addressSizeMin.errors["state"]
        "size" == addressSizeMin.errors["country"]
        "size" == addressSizeMin.errors["zip"]
        "size" == addressSizeMax.errors["street"]
        "size" == addressSizeMax.errors["city"]
        "size" == addressSizeMax.errors["state"]
        "size" == addressSizeMax.errors["country"]
        "size" == addressSizeMin.errors["zip"]
    }
}
