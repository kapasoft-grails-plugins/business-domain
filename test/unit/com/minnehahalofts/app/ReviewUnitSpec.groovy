package com.minnehahalofts.app

import grails.plugin.spock.UnitSpec
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.Mock
import org.joda.time.LocalDate

@TestFor(Review)
@TestMixin(GrailsUnitTestMixin)
@Mock(Review)
class ReviewUnitSpec extends UnitSpec{

    def "Enforcing Constraints"() {
        setup:
        mockForConstraintsTests(Review)
        def timeSer = mockFor(TimeService)
        timeSer.demand.currentDate(2) { ->
            return LocalDate.now()
        }

        when: "none nullable constraint ensured"
        def reviewNonNullable = new Review()
        reviewNonNullable.timeService = timeSer.createMock()
        then:
        assert !reviewNonNullable.validate()
        "nullable" == reviewNonNullable.errors["submittedBy"]
        "nullable" == reviewNonNullable.errors["content"]
        "nullable" == reviewNonNullable.errors["title"]

        when: "no Blank constraint ensured"
        def reviewNoBlank = new Review(submittedBy:' ', content:' ', title:' ', imgUrl:' ', dateReceived: new LocalDate(2011,3,3))
        then:
        assert !reviewNoBlank.validate()
        "blank" == reviewNoBlank.errors["submittedBy"]
        "blank" == reviewNoBlank.errors["content"]
        "blank" == reviewNoBlank.errors["title"]
        "blank" == reviewNoBlank.errors["imgUrl"]

        when: "size limit constraint ensured"
        def reviewSizedMin = new Review(submittedBy: 'ab', content:'abcd', title:'ab', recommendedFor: 'ab', dateReceived: new LocalDate(2011,3,3))
        def reviewSizedMax = new Review(submittedBy: 'x'*51, content: 'x'*2501, title:'x'*256, recommendedFor:'x'*256, dateReceived: new LocalDate(2011,3,3))
        then:
        assert !reviewSizedMin.validate()
        assert !reviewSizedMax.validate()
        "size" == reviewSizedMin.errors["submittedBy"]
        "size" == reviewSizedMin.errors["content"]
        "size" == reviewSizedMin.errors["title"]
        "size" == reviewSizedMin.errors["recommendedFor"]
        "size" == reviewSizedMax.errors["submittedBy"]
        "size" == reviewSizedMax.errors["content"]
        "size" == reviewSizedMax.errors["title"]
        "size" == reviewSizedMax.errors["recommendedFor"]

        when: "date constraint for dateReceived ensured"
        def dateValidation = new Review(dateReceived: LocalDate.now().plusDays(1), submittedBy: 'John', content:'some content comes here', title:'ab', recommendedFor: 'all kind of things')
        then:
        assert !dateValidation.validate()
        "validator" == dateValidation.errors["dateReceived"]

        when: "ensure dateReceived is generated if not provided"
        def dateGenValidation = new Review(submittedBy: 'John', content:'some content comes here', title:'abcde', recommendedFor: 'all kind of things')
        dateGenValidation.timeService = timeSer.createMock()
        then:
        assert dateGenValidation.validate()
        assert dateGenValidation.dateReceived
    }
}
