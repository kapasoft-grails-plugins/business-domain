package com.minnehahalofts.app

import grails.plugin.spock.IntegrationSpec
import org.springframework.dao.InvalidDataAccessApiUsageException

class InquiryIntegrationSpec extends IntegrationSpec{

    def "save and retrieve Inquiry"(){
        setup:
        def inquiry = Inquiry.build(userEmail:'test@host.com').save(flush: true)
        def rentalUnit = RentalUnit.build().addToInquiries(inquiry).save(flush: true)

        when: "finding Inquiry..."
        assert inquiry.id == rentalUnit.inquiries.first().id //ensure reference is present
        def foundInq = Inquiry.get(inquiry.id)

        then: "comparing email..."
        "test@host.com" == foundInq.userEmail
    }

    def "update Inquiry"(){
        setup:
        def inquiry = Inquiry.build().save(flush: true)
        def rentalUnit = RentalUnit.build().addToInquiries(inquiry).save(flush: true)

        when: "updating..."
        inquiry.userEmail = 'test2@host.com'
        inquiry.save(flush: true)

        then:
        "test2@host.com" == Inquiry.get(inquiry.id).userEmail
        "test2@host.com"  == rentalUnit.inquiries.first().userEmail
    }

    def "delete Inquiry with all associations present"(){
        setup:
        def inquiry = Inquiry.build().save(flush: true)
        def inquiry2 = Inquiry.build().save(flush: true)
        def rentalUnit = RentalUnit.build().save(flush: true)

        when: "delete review without associations(orphan)"
        assert Inquiry.exists(inquiry.id)
        inquiry.delete(flush: true)

        then:
        !Inquiry.exists(inquiry.id)

        when: "delete as none orphan"
        rentalUnit.addToInquiries(inquiry2).save(flush: true)
        assert 1 == rentalUnit.inquiries.size()
        assert Inquiry.exists(inquiry2.id)
        inquiry2.delete(flush: true)

        then: "has reference from RentalUnit so not permited to delete"
        thrown(InvalidDataAccessApiUsageException)
        1 == rentalUnit.inquiries.size()
        1 == Inquiry.count()
    }
}
