package reviews

import grails.converters.JSON
import com.minnehahalofts.app.Review

class ReviewMarshaller {

    void register(){
        JSON.registerObjectMarshaller(Review) {Review review ->
            return [
                    id : review.id,
                    title : review?.title,
                    content : review?.content,
                    rentalUnitId : review?.rentalUnit?.id,
                    version: review.version,
                    submittedBy: review.submittedBy,
                    dateReceived : review?.dateReceived.toString("yyyy-MM-dd")
            ]
        }

    }
}
