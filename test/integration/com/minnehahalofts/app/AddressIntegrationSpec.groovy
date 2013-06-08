
package com.minnehahalofts.app

import grails.plugin.spock.IntegrationSpec
//import org.springframework.dao.InvalidDataAccessApiUsageException
//import org.hibernate.ObjectDeletedException
//import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException

class AddressIntegrationSpec extends IntegrationSpec{

    def "save and retrieve Address"(){
        setup:
        def address = Address.build(street:"3324 23rd Ave S").save(flush: true)

        when: "finding Address..."
        def foundAddress = Address.get(address.id)

        then: "comparing street..."
        "3324 23rd Ave S" == foundAddress.street
    }

    def "update Address"(){
        setup:
        def address = Address.build(street:"3324 23rd Ave S").save(flush: true)

        when: "updating..."
        address.street = '2816 32nd Ave S'
        address.save(flush: true)

        then:
        "2816 32nd Ave S" == Address.get(address.id).street
    }

    def "delete Address with Associations present"() {
        given:
        def address1 = Address.build(street:"3324 23rd Ave S").save(flush: true)
        def address2 = Address.build(street:"3324 23rd Ave S").save(flush: true)
        def address3 = Address.build(street:"2816 32nd ave s").save(flush: true)
        def rUnit = RentalUnit.build(address: address2).save(flush: true)

        when: "deleting address without associations present. Expected:address deleted"
        assert Address.find(address1)
        address1.delete(flush: true)

        then:
        !Address.exists(address1.id)

        when: "deleting address(orphan) with associations present. Expected:address deleted"
        rUnit.address=address3
        rUnit.save(flush: true)
        assert Address.exists(address2.id)
        address2.delete(flush: true)

        then:
        !Address.exists(address2.id)

        when:"deleting address(none-orphan) with associations present. Expected: unable to delete address"
        assert Address.exists(address3.id)
        assert rUnit.address.id == address3.id
        address3.delete(flush: true)

        then:
        thrown(Exception)
        Address.exists(address3.id)
    }

    def "one-to-one from RentalUnit to Address ensured"() {
        given:
        def rentalUnit = RentalUnit.build().save(flush: true)
        def address1 = Address.build(street:"3324 23rd Ave S").save(flush: true)
        def address2 = Address.build(street:"3324 23rd Ave S").save(flush: true)

        when: "ensure single Rental Unit referenced by single Address"
        address1.rentalUnit = rentalUnit
        address2.rentalUnit = rentalUnit
        address1.save(flush: true)
        address2.save(flush: true)
        address1.refresh()

        then:
        !address1.rentalUnit
        address2.rentalUnit.id == rentalUnit.id
    }

    /***********Company************/

    def "ensure no cascading on Company"() {
        given:
        def address = Address.build().save(flush: true)
        def company = Company.build().save(flush: true)
        address.company = company

        when:
        assert company.id == address.company.id
        assert !company.address
        assert 1 == Company.count()
        assert 1 == Address.count()
        address.delete()

        then:
        assert !Address.exists(address.id)
        assert 0 == Address.count()
        assert 1 == Company.count()
    }


}
