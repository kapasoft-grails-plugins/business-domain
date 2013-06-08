package contacts

import grails.converters.JSON
import com.minnehahalofts.app.ContactUs

class ContactUsMarshaller {

    void register(){
        JSON.registerObjectMarshaller(ContactUs) {ContactUs contactUs ->
            return [
                    id : contactUs.id,
                    name : contactUs?.name,
                    message : contactUs?.message,
                    phone : contactUs?.phone,
                    version: contactUs.version,
                    contactDate : contactUs?.contactDate.toString("yyyy-MM-dd")
            ]
        }

    }
}
