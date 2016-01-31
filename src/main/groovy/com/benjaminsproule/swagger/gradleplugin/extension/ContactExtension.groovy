package com.benjaminsproule.swagger.gradleplugin.extension

import groovy.transform.ToString

@ToString(includeNames = true)
class ContactExtension {
    String name
    String url
    String email
}
