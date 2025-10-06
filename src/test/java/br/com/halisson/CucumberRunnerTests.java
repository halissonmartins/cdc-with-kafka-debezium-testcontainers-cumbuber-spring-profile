package br.com.halisson;


import org.junit.platform.suite.api.*;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters({
        @ConfigurationParameter(
                key = PLUGIN_PROPERTY_NAME,
                value = "pretty, html:target/cucumber-reports/cucumber.html, " +
                        "junit:target/cucumber-reports/cucumber.xml, " +
                        "json:target/cucumber-reports/cucumber.json"
        ),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.halisson"),
        @ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@controller"),
        @ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true"),
        @ConfigurationParameter(key = JUNIT_PLATFORM_NAMING_STRATEGY_PROPERTY_NAME, value = "long")
})
@EnabledIf(expression = "#{environment.acceptsProfiles('bdd')}", loadContext = false)
public class CucumberRunnerTests {
}
