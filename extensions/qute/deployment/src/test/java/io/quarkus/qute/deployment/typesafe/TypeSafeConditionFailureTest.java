package io.quarkus.qute.deployment.typesafe;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateException;
import io.quarkus.qute.deployment.Foo;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TypeSafeConditionFailureTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClass(Foo.class)
                    .addAsResource(new StringAsset("{@String unusedParameter = \"var\"}"
                            + "{#if bazbaz.size == 1}"
                            + "unreachable code"
                            + "{/}"), "templates/foo.html"));

    @Inject
    Template foo;

    @Test
    public void testValidation() {
        List<String> bazbaz = null; // initialize the variable with the appropriate type but null value
        try {
            foo.data("bazbaz", bazbaz).render();
        } catch(TemplateException e) {
            assertFalse(e.getMessage().contains("timeout"), "Exception message was: " + e.getMessage());
            assertEquals("Property \"bazbaz\" not found on the base object \"null\"", e.getMessage().trim());
        }
    }
}