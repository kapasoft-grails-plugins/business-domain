package com.minnehahalofts.app

import java.text.ParseException
import org.joda.time.LocalDate

class Review {
    //service to proxy driver
//    def nodeDriverProxyService
    //enables this object for being cached
    static final boolean isCached = true
    def timeService
    String submittedBy
    String content
    LocalDate dateReceived
    boolean isApproved
    String title
    String recommendedFor
    String imgUrl

    static belongsTo = [rentalUnit: RentalUnit]

    static mapping = {
        content type: 'text'
    }

    static constraints = {
        imgUrl blank: false, nullable: true
        submittedBy blank: false, size: 3..50
        content blank: false, size: 5..2500
        dateReceived nullable: true, validator: {value, obj ->
            if(!value){
                obj.dateReceived = obj.timeService.currentDate()
                return true
            }else{
                value.isBefore(LocalDate.now()) || value.isEqual(LocalDate.now())
            }
        }
        rentalUnit nullable: true
        title blank: false, size: 3..255
        recommendedFor blank: true, nullable: true, size: 3..255
    }

    def propertyMissing( name ) {
        null
    }

    String getImgUrl(){
        if(!imgUrl && this.rentalUnit){
            imgUrl = 'images/minnehaha/' + this.rentalUnit.name + '/front_min.jpg'
        }else if(!imgUrl){//must not have renatal unit assigned
            imgUrl = 'images/minnehaha/front_min.jpg'
        }

        if(this.rentalUnit && !imgUrl.contains(this.rentalUnit.name)){
            imgUrl = 'images/minnehaha/' + this.rentalUnit.name + '/front_min.jpg'
        }

        imgUrl
    }

}
