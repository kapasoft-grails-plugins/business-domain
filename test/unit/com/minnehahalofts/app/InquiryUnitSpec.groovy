package com.minnehahalofts.app

import grails.buildtestdata.mixin.Build
import grails.plugin.spock.UnitSpec
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import java.text.DateFormat
import org.joda.time.LocalDate

@TestFor(Inquiry)
@TestMixin(GrailsUnitTestMixin)
@Build(Inquiry)
class InquiryUnitSpec extends UnitSpec{

    def "enforcing constraint"(){
        setup:
        def  existingInquiry = new Inquiry(userEmail: "testhost.com", submitDate: DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date()), inqContent: 'can you call us')
        mockForConstraintsTests(Inquiry, [existingInquiry])
        def timeSer = mockFor(TimeService)
        timeSer.demand.currentDate(2) { ->
            return LocalDate.now()
        }

        when:"nullable constraint ensured"
        def inqSampleNullable = new Inquiry()
        inqSampleNullable.timeService = timeSer.createMock()
        then:
        assert !inqSampleNullable.validate()
        "nullable" == inqSampleNullable.errors["userEmail"]
        "nullable" == inqSampleNullable.errors["inqContent"]


        when: "email constraint ensured"
        def inqSampleEmail = new Inquiry(userEmail: "testhost.com", submitDate: new LocalDate(2011,3,3), inqContent: 'can you call us')
        then:
        assert !inqSampleEmail.validate()
        "email" == inqSampleEmail.errors["userEmail"]

        when: "No blank email constraint ensured"
        def inqSampleEmailBalnk = new Inquiry(userEmail: "", submitDate: new LocalDate(2011,3,3), inqContent: 'can you call us')
        then:
        assert !inqSampleEmailBalnk.validate()
        "email" == inqSampleEmail.errors["userEmail"]

        when: "date constraint for submitDate ensured"
        def inqSampleDate2 = new Inquiry(userEmail: "test@host.com", submitDate: LocalDate.now().plusDays(1), inqContent: 'can you call us')
        then:
        assert !inqSampleDate2.validate()
        "validator" == inqSampleDate2.errors["submitDate"]

        when: "No blank constraint for inqContent ensured"
        def inqSampleBlank = new Inquiry(userEmail: "test@host.com", submitDate: new LocalDate(2011,3,3), inqContent: '')
        then:
        assert !inqSampleBlank.validate()
        "blank" == inqSampleBlank.errors["inqContent"]

        when: "size limit constraint ensured"
        def inquirySizeMin = new Inquiry(userEmail: "m@h.lv", inqContent: 'x'*5, submitDate: new LocalDate(2011,3,3))
        def inquirySizeMax = new Inquiry(userEmail: 'x'*23+'@h.com', inqContent: 'x'*2501, submitDate: new LocalDate(2011,3,3))
        then:
        assert !inquirySizeMin.validate()
        assert !inquirySizeMax.validate()
        "size" == inquirySizeMin.errors["userEmail"]
        "size" == inquirySizeMin.errors["inqContent"]
        "size" == inquirySizeMax.errors["userEmail"]
        "size" == inquirySizeMax.errors["inqContent"]

        when: "ensure submitDate is generated if not provided"
        def dateGenValidation = new Inquiry(userEmail: "test@host.com", inqContent: 'Some inquiry will come here. Oki Doki')
        dateGenValidation.timeService = timeSer.createMock()
        then:
        assert dateGenValidation.validate()
        assert dateGenValidation.submitDate
    }

    def "find inquiry by user email or submission date"(){
        setup:"at setup inserting inquiry to be searched"
        def someDate =  new LocalDate(2011,3,3)
        mockDomain(Inquiry,[
                Inquiry.build(userEmail: "test@host.com", submitDate: someDate),
                Inquiry.build(userEmail: "test2@host.com", submitDate: someDate)
        ])
        assert 2 == Inquiry.count()

        when: "searching for inquiry"
        def foundInq1 = Inquiry.findByUserEmail("test@host.com")
//        def foundInq2 = Inquiry.findBySubmitDate(someDate)

        then:
        assert foundInq1
//        assert foundInq2
    }
}
