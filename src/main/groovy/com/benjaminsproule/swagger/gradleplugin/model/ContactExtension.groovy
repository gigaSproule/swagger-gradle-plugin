package com.benjaminsproule.swagger.gradleplugin.model

import groovy.transform.ToString
import io.swagger.models.Contact

@ToString(includeNames = true)
class ContactExtension {
    String name
    String url
    String email

    Contact getSwaggerContact() {
        Contact contact = new Contact()
        contact.setName(name)
        contact.setUrl(url)
        contact.setEmail(email)

        return contact
    }
}
