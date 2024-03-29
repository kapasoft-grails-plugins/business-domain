import com.minnehahalofts.app.Address
import com.minnehahalofts.app.Company
import com.minnehahalofts.app.ContactUs
import com.minnehahalofts.app.Inquiry
import com.minnehahalofts.app.RentalUnit
import com.minnehahalofts.app.Review
import contacts.ContactUsMarshaller
import inquiry.InquiryMarshaller
import reviews.ReviewMarshaller
import util.marshalling.CustomObjectMarshallers

class BusinessDomainGrailsPlugin {
    def groupId = 'minnehaha'
    // the plugin version
    def version = "0.1-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Business Domain Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/business-domain"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {

        address(Address){
//            it.autowire = true
        }

        company(Company){
//            it.autowire = true
        }

        contactUs(ContactUs){
            timeService = ref('timeService')
//            it.autowire = true
        }

        inquiry(Inquiry){
            timeService = ref('timeService')
//            it.autowire = true
        }

        rentalUnit(RentalUnit){
//            it.autowire = true
        }

        review(Review){
            timeService = ref('timeService')
//            it.autowire = true
        }

        contactUsMarshaller(ContactUsMarshaller){}

        inquiryMarshaller(InquiryMarshaller){}

        reviewMarshaller(ReviewMarshaller){}

        customObjectMarshallers( CustomObjectMarshallers ) {
            marshallers = [
                    new ContactUsMarshaller(),
                    new InquiryMarshaller(),
                    new ReviewMarshaller()
            ]
        }

        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
        applicationContext.getBean( "customObjectMarshallers" ).register()
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
