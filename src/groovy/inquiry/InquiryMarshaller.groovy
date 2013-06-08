package inquiry

import grails.converters.JSON
import com.minnehahalofts.app.Inquiry

class InquiryMarshaller {
    void register(){
        JSON.registerObjectMarshaller(Inquiry) {Inquiry inquiry ->
            return [
                    id : inquiry.id,
                    inqContent : inquiry?.inqContent,
                    version: inquiry.version,
                    userEmail: inquiry.userEmail,
                    submitDate : inquiry?.submitDate.toString("yyyy-MM-dd")
            ]
        }

    }
}
