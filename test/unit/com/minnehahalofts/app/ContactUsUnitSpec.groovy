package com.minnehahalofts.app

import grails.test.mixin.TestFor

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.plugin.spock.UnitSpec
import org.joda.time.LocalDate


@TestFor(ContactUs)
@TestMixin(GrailsUnitTestMixin)
class ContactUsUnitSpec extends UnitSpec {

    def "ensure all constraints"() {
        setup:
        def  connection = new ContactUs(name: "Mary Johns", email: "min@hotmail.com", phone: "12345678", message: "long message comes here", contactDate: new LocalDate())
        mockForConstraintsTests(ContactUs, [connection])
        def timeSer = mockFor(TimeService)
        timeSer.demand.currentDate(2) { ->
            return LocalDate.now()
        }

        when: "none nullable constraint ensured"
        def connectionNotNullable = new ContactUs()
        connectionNotNullable.timeService = timeSer.createMock()

        then:
        !connectionNotNullable.validate()
        "nullable" == connectionNotNullable.errors["name"]
        "nullable" == connectionNotNullable.errors["email"]
        "nullable" == connectionNotNullable.errors["message"]

        when: "no Blank constraint ensured"
        def connectionNoBlank = new ContactUs(name:' ', message:' ', phone: ' ', email: ' ', contactDate: new LocalDate())

        then:
        assert !connectionNoBlank.validate()
        "blank" == connectionNoBlank.errors["name"]
        "blank" == connectionNoBlank.errors["message"]
        "blank" == connectionNoBlank.errors["phone"]
        "blank" == connectionNoBlank.errors["email"]

        when: "size limits ensured"
        def connectionSizedMin =  new ContactUs(name:'Mi', message:'abc', phone:'651334633', email:'s@h.l', contactDate: new LocalDate())
        def connectionSizedMax =  new ContactUs(name:'M'*51, message: 'C'*2501, phone:'6'*17, email:'s'*47+'@h.com', contactDate: new LocalDate())

        then:
        !connectionSizedMin.validate()
        !connectionSizedMax.validate()
        "size" == connectionSizedMin.errors["name"]
        "size" == connectionSizedMin.errors["message"]
        "size" == connectionSizedMin.errors["phone"]
        "size" == connectionSizedMin.errors["email"]
        "size" == connectionSizedMax.errors["name"]
        "size" == connectionSizedMax.errors["message"]
        "size" == connectionSizedMax.errors["phone"]
        "size" == connectionSizedMax.errors["email"]

        when:"email ensured"
        def connectionBadEmail = new ContactUs(name: 'someName', message: "nikky is fun name", email: "ata@dadas", contactDate: LocalDate.now())

        then:
        !connectionBadEmail.validate()
        "email" == connectionBadEmail.errors["email"]

        when:"phone number ensured"
        def connectionBadPhone = new ContactUs(name: 'someName', message: "nikky is fun name", email: "ata@dadas", contactDate: LocalDate.now(), phone: '65AB388333333')

        then:
        !connectionBadPhone.validate()
        "matches" == connectionBadPhone.errors["phone"]

        when: "date constraint for contactDate ensured"
        def dateValidation = new ContactUs(name: 'someName', message: "nikky is fun name", email: "ata@dadas", contactDate: LocalDate.now().plusDays(1), phone: '65AB388333333')
        then:
        assert !dateValidation.validate()
        "validator" == dateValidation.errors["contactDate"]

        when: "ensure contactDate is generated if not provided"
        def dateGenValidation = new ContactUs(name: "Mary Johns", email: "min@hotmail.com", phone: "1234567856", message: "long message comes here")
        dateGenValidation.timeService = timeSer.createMock()
        then:
        assert dateGenValidation.validate()
        assert dateGenValidation.contactDate
    }
}