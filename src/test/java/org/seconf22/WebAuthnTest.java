package org.seconf22;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.virtualauthenticator.HasVirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions.Protocol;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions.Transport;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WebAuthnTest {

    private static WebDriver driver;
    private static VirtualAuthenticator virtualAuthenticator;

    @BeforeAll
    static void setup() {
        driver = WebDriverManager.chromedriver().create();
    }

    @Test
    @DisplayName("WebAuthn reg and auth flow should work")
    void sampleTest() throws InterruptedException {

        // set up the virtual authenticator for webauthn
        virtualAuthenticator = setupVirtualAuthenticator();

        // start registration
        driver.get("https://webauthn.io");
        driver.findElement(By.id("input-email")).sendKeys("seconf22");

        Select selectAttestationType = new Select(driver.findElement(By.id("select-attestation")));
        selectAttestationType.selectByVisibleText("Direct");

        Select selectAuthenticatorType = new Select(driver.findElement(By.id("select-authenticator")));
        selectAuthenticatorType.selectByVisibleText("Platform (TPM)");

        driver.findElement(By.id("register-button")).click();

        // registration should be successful
        Thread.sleep(3000);
        assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Success!')]")).isDisplayed());

        // start authentication
        driver.findElement(By.id("login-button")).click();

        // authentication should be successful
        Thread.sleep(3000);
        assertTrue(driver.findElement(By.xpath("//h3[text()=\"You're logged in!\"]")).isDisplayed());
    }

    private VirtualAuthenticator setupVirtualAuthenticator() {
        VirtualAuthenticatorOptions options = new VirtualAuthenticatorOptions();
        options.setTransport(Transport.INTERNAL)
                .setProtocol(Protocol.CTAP2)
                .setHasUserVerification(true)
                .setIsUserVerified(true);
        return ((HasVirtualAuthenticator) driver).addVirtualAuthenticator(options);
    }

    @AfterAll
    static void cleanup() {
        virtualAuthenticator.removeAllCredentials();
        driver.quit();
    }
}