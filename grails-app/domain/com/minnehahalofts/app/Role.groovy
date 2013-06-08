package com.minnehahalofts.app

class Role {

    static final boolean isCached = false
	String authority

	static mapping = {
		cache true
	}

	static constraints = {
		authority blank: false, unique: true
	}

    def propertyMissing( name ) {
        null
    }
}
