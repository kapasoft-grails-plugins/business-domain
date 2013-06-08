import java.text.DateFormat
import org.joda.time.LocalDate

testDataConfig {
    sampleData {
        'com.minnehahalofts.app.Inquiry'{
            submitDate = new LocalDate(2011, 3, 3)
        }
        'com.minnehahalofts.app.RentalUnit'{
            def i = 1
            name = {-> "name${i++}" }
        }

        'com.minnehahalofts.app.Review'{
            dateReceived = new LocalDate(2011, 3, 3)
            isApproved = false
        }

        'com.minnehahalofts.app.Address'{
            zip = "55407"
        }

        'com.minnehahalofts.app.ContactUs'{
            phone = "1234567890"
            contactDate = new LocalDate(2012,1,1)
        }

        'com.minnehahalofts.app.Company'{
            def i=1
            def j=1
            nickname = {-> "name${i++}" }
            fullName = {-> "fullName${j++}"}
        }
    }
}

/*
// sample for creating a single static value for the com.foo.Book's title property:
// title for all Books that we "build()" will be "The Shining", unless explicitly set

testDataConfig {
    sampleData {
        'com.foo.Book' {
            title = "The Shining"
        }
    }
}
*/

/*
// sample for creating a dynamic title for com.foo.Book, useful for unique properties:
// just specify a closure that gets called

testDataConfig {
    sampleData {
        'com.foo.Book' {
            def i = 1
            title = {-> "title${i++}" }   // creates "title1", "title2", ...."titleN"
        }
    }
}
*/

/*
// When using a closure, if your tests expect a particular value, you'll likely want to reset
// the build-test-data config in the setUp of your test, or in the test itself.  Otherwise if
// your tests get run in a different order you'll get different values

// (in test/integration/FooTests.groovy)

void setUp() {
    grails.buildtestdata.TestDataConfigurationHolder.reset()
}
*/


/*
// if you'd like to disable the build-test-data plugin in an environment, just set
// the "enabled" property to false

environments {
    production {
        testDataConfig {
            enabled = false
        }
    }
}
*/