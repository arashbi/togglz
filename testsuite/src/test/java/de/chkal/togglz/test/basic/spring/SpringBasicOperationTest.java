package de.chkal.togglz.test.basic.spring;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;

import de.chkal.togglz.test.basic.FeatureServlet;
import de.chkal.togglz.test.basic.BasicFeatures;

@RunWith(Arquillian.class)
public class SpringBasicOperationTest {

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addAsLibraries(
                        ShrinkWrap.create(ZipImporter.class, "togglz-core.jar")
                                .importFrom(new File("../core/target/togglz-core-1.0-SNAPSHOT.jar"))
                                .as(JavaArchive.class),
                        ShrinkWrap.create(ZipImporter.class, "togglz-spring.jar")
                                .importFrom(new File("../spring/target/togglz-spring-1.0-SNAPSHOT.jar"))
                                .as(JavaArchive.class),
                        ShrinkWrap.create(ZipImporter.class, "togglz-servlet.jar")
                                .importFrom(new File("../servlet/target/togglz-servlet-1.0-SNAPSHOT.jar"))
                                .as(JavaArchive.class))
                .addAsLibraries(
                        DependencyResolvers.use(MavenDependencyResolver.class)
                                .artifact("org.slf4j:slf4j-jdk14:1.6.4")
                                .resolveAs(JavaArchive.class))
                .addAsLibraries(
                        DependencyResolvers.use(MavenDependencyResolver.class)
                                .artifact("org.springframework:spring-web:3.0.7.RELEASE")
                                .resolveAs(JavaArchive.class))
                .addClass(SpringFeatureConfiguration.class)
                .addClass(FeatureServlet.class)
                .addClass(BasicFeatures.class)
                .addAsWebInfResource("common/spring/applicationContext.xml")
                .setWebXML("common/spring/spring-web.xml")
                ;
    }
    
    @ArquillianResource
    private URL url;

    @Test
    public void testBasicFeatures() throws IOException {

        WebClient client = new WebClient();
        TextPage page = client.getPage(url + "features");
        assertTrue(page.getContent().contains("FEATURE1 = false"));
        assertTrue(page.getContent().contains("FEATURE2 = true"));

    }

}
