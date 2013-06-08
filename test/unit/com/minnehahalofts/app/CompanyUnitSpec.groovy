package com.minnehahalofts.app

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.buildtestdata.mixin.Build
import grails.plugin.spock.UnitSpec

@TestFor(Company)
@TestMixin(GrailsUnitTestMixin)
@Build([Address])
class CompanyUnitSpec extends UnitSpec {

    def "ensure all constraints"() {
        setup:
        def  company = new Company(fullName: "Minnehaha Lofts LLC", nickname: "minnehaha", phone: "12345678", email:"kd@hotmail.com", address: Address.build())
        mockForConstraintsTests(Company, [company])

        when: "none nullable constraint ensured"
        def companyNotNullable = new Company()

        then:
        !companyNotNullable.validate()
        "nullable" == companyNotNullable.errors["fullName"]
        "nullable" == companyNotNullable.errors["nickname"]

        when: "no Blank constraint ensured"
        def companyNoBlank = new Company(fullName:' ', nickname:' ', phone: ' ', email: ' ')

        then:
        assert !companyNoBlank.validate()
        "blank" == companyNoBlank.errors["fullName"]
        "blank" == companyNoBlank.errors["nickname"]
        "blank" == companyNoBlank.errors["phone"]
        "blank" == companyNoBlank.errors["email"]

        when: "unique name ensured"
        def companyUniqueName = new Company(nickname:'minnehaha', fullName:'Minnehaha Lofts LLC')
        then:
        assert !companyUniqueName.validate()
        "unique" == companyUniqueName.errors["fullName"]
        "unique" == companyUniqueName.errors["nickname"]

        when: "size limits ensured"
        def companySizedMin =  new Company(fullName:'Min', nickname:'Ch', phone:'651334633', email:'s@h.l')
        def companySizedMax =  new Company(fullName:'M'*51, nickname:'C'*16, phone:'6'*17, email:'s'*47+'@h.com')
        then:
        !companySizedMin.validate()
        !companySizedMax.validate()
        "size" == companySizedMin.errors["fullName"]
        "size" == companySizedMin.errors["nickname"]
        "size" == companySizedMin.errors["phone"]
        "size" == companySizedMin.errors["email"]
        "size" == companySizedMax.errors["fullName"]
        "size" == companySizedMax.errors["nickname"]
        "size" == companySizedMax.errors["phone"]
        "size" == companySizedMax.errors["email"]

        when:"email ensured"
        def companyBadEmail = new Company(fullName: 'someName', nickname: "nikky", email: "ata@dadas")

        then:
        !companyBadEmail.validate()
        "email" == companyBadEmail.errors["email"]

        when:"phone number ensured"
        def companyBadPhone = new Company(fullName: 'someName', nickname: "nikky", phone: '65AB388333333')

        then:
        !companyBadPhone.validate()
        "matches" == companyBadPhone.errors["phone"]
    }
}
