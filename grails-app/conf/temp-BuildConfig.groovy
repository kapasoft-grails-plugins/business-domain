grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

//org.apache.log4j.BasicConfigurator.configure();
grails.server.port.http=8010

//external plugins
grails.plugin.location.RestApiForDomains='min_plugins/restApiForDomains'
grails.plugin.location.CacheDomains='min_plugins/CacheDomains'

grails.project.dependency.resolution = {
    log "warn"
    checksums false
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
//    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
//        mavenRepo "http://localhost:8081/nexus/content/repositories"
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        runtime 'mysql:mysql-connector-java:5.1.16'
        compile 'joda-time:joda-time:2.1'
        compile('joda-time:joda-time-hibernate:1.3') {
            excludes 'hibernate', 'joda-time'
        }

        runtime 'org.codehaus.groovy.modules.http-builder:http-builder:0.6'
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":resources:1.1.6"
		runtime ":cached-resources:1.0"
		runtime ":zipped-resources:1.0"
		runtime ":yui-minify-resources:0.1.4"
		runtime ":spring-security-core:1.2.7.3"
		runtime ":cache-headers:1.1.5"
        runtime ":database-migration:1.1"

//        test ":spock:0.6"

        build ":tomcat:$grailsVersion"
        compile ":twitter-bootstrap:2.2.1"
//        compile "minnehaha:cache-domains:0.3-SNAPSHOT"
//        compile "minnehaha:rest-api-for-domains:0.1-SNAPSHOT"
    }

    /*****Spock Config*****/
    grails.project.dependency.resolution = {
        repositories {
            grailsCentral()
            mavenCentral()
        }
        plugins {
            test ":spock:0.7"
        }
    }
}
