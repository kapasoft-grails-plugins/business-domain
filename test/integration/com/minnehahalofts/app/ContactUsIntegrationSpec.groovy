package com.minnehahalofts.app

import grails.plugin.spock.IntegrationSpec

class ContactUsIntegrationSpec extends IntegrationSpec {

    void "validated setupContactUs() method"() {
        when:
        def contactUs = setupContactUs()

        then:
        1 == ContactUs.count()
        1 == Company.count()
        contactUs.id == contactUs.company.contactUs.first().id
    }

    def "save and retrieve ContactUs with all associations present"(){
        given:
        def contactUs = setupContactUs('test')

        when: "finding ContactUs..."
        def foundContactUs = ContactUs.get(contactUs.id)

        then: "comparing name..."
        1 == ContactUs.count()
        'test' == foundContactUs.name
    }

    def "update ContactUs with all associations present"(){
        setup:
        def contactUs = setupContactUs()

        when: "updating..."
        contactUs.name = 'Rico'
        contactUs.save(flush: true)

        then:
        "Rico" == ContactUs.get(contactUs.id).name
    }

    def "delete ContactUs with all associations present"(){
        setup:
        def contactUs = setupContactUs()

        when: "deleting..."
        1 == ContactUs.count()
        1 == Company.count()
        contactUs.delete(flush: true)

        then:
        thrown(Exception)/*association to Company prohibits to delete*/
    }

/**********Company*********/
    def "ensure no cascading on company"() {
        given:
        def contactUs = setupContactUs()

        when:
        assert contactUs.company
        assert 1 == ContactUs.count()
        assert 1 == Company.count()
        contactUs.company.contactUs.clear()
        assert 0 == contactUs.company.contactUs.size()//remove reference from company
        contactUs.delete(flush: true)

        then:
        !ContactUs.exists(contactUs.id)
        0 == ContactUs.count()
        1 == Company.count()
    }


    ContactUs setupContactUs(String name = 'John'){
        def company = Company.build()
        def contactUs = ContactUs.build(name: name).save(flush: true)
        company.addToContactUs(contactUs).save(flush: true)
        return contactUs
    }
}