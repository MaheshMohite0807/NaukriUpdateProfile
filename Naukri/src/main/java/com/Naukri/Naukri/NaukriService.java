package com.Naukri.Naukri;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import io.github.bonigarcia.wdm.WebDriverManager;

@Service
public class NaukriService implements ApplicationListener<ApplicationReadyEvent>{

	public static WebDriver driver;
	
	@Value("${naukri.username}")
	private String username;
	
	@Value("${naukri.password}")
	private String password;
	
	@Value("${naukri.resume.path}")
	private String resumePath;
	
	private boolean isInitialized = false;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
    	inititializeBrowser();
    }
    
    
	public int inititializeBrowser() {
	
		if(!isInitialized) {
		 WebDriverManager.chromedriver().setup();
			
			driver = new ChromeDriver();
			
			driver.get("https://www.naukri.com/");
			
			driver.manage().window().maximize();
			
            handleLoginPopup();
            
            // Click on login button
            try {
				clickLoginButton();
			} catch (InterruptedException e) {
				System.out.println("ERROR : "+ e);
			}
            
            switchToLoginIframe();
            
            // Enter username 
            try {
				enterUsername();
			} catch (Exception e) {
				System.out.println("ERROR : "+ e);
			}
            
            // Enter password
            try {
				enterPassword();
			} catch (Exception e) {
				System.out.println("ERROR : "+ e);
			}
            
            // Click submit
            try {
				clickLoginSubmit();
			} catch (InterruptedException e) {
				System.out.println("ERROR : "+ e);
			}
            
            navigateToProfile();

			isInitialized = true;
			
		}
		return 1;
	}
	
	 private void handleLoginPopup() {
	        try {
	            List<WebElement> popupCloseButtons = driver.findElements(
	                By.xpath("//*[contains(@class,'close') or contains(text(),'×')]"));
	            
	            if (!popupCloseButtons.isEmpty()) {
	                popupCloseButtons.get(0).click();
	                System.out.println("Closed popup");
	            }
	        } catch (Exception e) {
	            System.out.println("No popup to close or couldn't close it");
	        }
	    }

	 
	 private void clickLoginButton() throws InterruptedException {
	        try {
	            WebElement loginBtn = new WebDriverWait(driver, Duration.ofSeconds(20))
	                .until(ExpectedConditions.elementToBeClickable(
	                    By.xpath("//a[contains(@href,'login') or contains(text(),'Login')]")));
	            
	            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
	            System.out.println("Clicked login button");
	            Thread.sleep(2000); 
	        } catch (TimeoutException e) {
	            System.out.println("Trying alternative login button location");
	            driver.get("https://www.naukri.com/nlogin/login");
	        }
	 }     

	 private void switchToLoginIframe() {
	        try {
	            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
	            for (WebElement iframe : iframes) {
	                driver.switchTo().frame(iframe);
	                try {
	                    if (!driver.findElements(By.id("usernameField")).isEmpty()) {
	                        System.out.println("Switched to login iframe");
	                        return;
	                    }
	                } finally {
	                    driver.switchTo().defaultContent();
	                }
	            }
	        } catch (Exception e) {
	            System.out.println("No login iframe found");
	        }
	    }
	 
	    private void enterUsername() throws Exception {
	        try {
	            WebElement usernameField = new WebDriverWait(driver, Duration.ofSeconds(15))
	                .until(ExpectedConditions.visibilityOfElementLocated(
	                    By.xpath("//input[@id='usernameField' or @name='email' or contains(@placeholder,'Email')]")));
	            
	            usernameField.clear();
	            
	            for (char c : username.toCharArray()) {
	                usernameField.sendKeys(String.valueOf(c));
	                Thread.sleep(100);
	            }
	            System.out.println("Entered username");
	        } catch (Exception e) {
	            System.out.println("Failed to enter username");
	            throw e;
	        }
	    }

	    
	    private void enterPassword() throws Exception {
	        try {
	        	  WebElement passwordField = new WebDriverWait(driver, Duration.ofSeconds(20))
	        	            .until(ExpectedConditions.visibilityOfElementLocated(
	        	                By.xpath("//input[@id='passwordField' or @placeholder='Enter your password']")));
	            
	        	  passwordField.clear();
	              passwordField.sendKeys(password);
	
	            System.out.println("Entered password");
	        } catch (Exception e) {
	            System.out.println("Failed to enter password");
	            throw e;
	        }
	    }
	    
	    private void clickLoginSubmit() throws InterruptedException {
	        try {
	            WebElement loginSubmit = new WebDriverWait(driver, Duration.ofSeconds(15))
	                .until(ExpectedConditions.elementToBeClickable(
	                    By.xpath("//button[contains(text(),'Login') or @type='submit']")));
	            
	            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loginSubmit);
	            Thread.sleep(500);
	            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginSubmit);
	            System.out.println("Clicked login submit");
	            Thread.sleep(3000);
	        } catch (Exception e) {
	            System.out.println("Failed to click login button");
	            throw e;
	        }
	    }
	    
	    private void navigateToProfile() {
	        try {
	            WebElement profileLink = new WebDriverWait(driver, Duration.ofSeconds(20))
	                .until(ExpectedConditions.elementToBeClickable(
	                    By.xpath("//a[contains(@href,'/mnjuser/profile') or contains(text(),'Profile')]")));
	            
	            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", profileLink);
	            
	            new WebDriverWait(driver, Duration.ofSeconds(20))
	                .until(ExpectedConditions.urlContains("profile"));
	            System.out.println("Navigated to profile section");
	        } catch (Exception e) {
	            System.out.println("Failed to navigate to profile");
	            throw e;
	        }
	    }
	    
	    public void updateProfile() {
	    	 if (driver != null) {
	    	        try {
	    	            deleteResume();
	    	            
	    	            uploadResume();
	    	            
	    	            System.out.println("Resume update completed successfully");
	    	        } catch (Exception e) {
	    	            System.out.println("Failed to update resume: " + e.getMessage());
	    	            e.printStackTrace();
	    	        }
	    	    }
	    }
	    

	    private void deleteResume() throws Exception {
	        try {
	        	WebElement deleteIcon = new WebDriverWait(driver, Duration.ofSeconds(2))
	                    .until(ExpectedConditions.elementToBeClickable(
	                        By.xpath("//i[@data-title='delete-resume']")));
	                
	                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteIcon);
	                System.out.println("Clicked delete resume icon");
	                
	                WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(5));
	                
	                handleConfirmationPopup(popupWait);
	                
	                verifyResumeDeletion();
	            System.out.println("Deleted resume");
	        } catch (Exception e) {
	            System.out.println("Failed to delete resume: " + e.getMessage());
	            throw e;
	        }
	    }

	    private void handleConfirmationPopup(WebDriverWait wait) throws Exception {
	        try {	            
	            WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
	                By.xpath("//button[contains(@class, 'btn-dark-ot') and contains(., 'Delete')]")));
	            deleteButton.click();
	            return;
	        } catch (TimeoutException e1) {
	            try {
	                WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(
	                    By.xpath("//div[contains(@class, 'lightbox') and contains(@class, 'model_open')]")));
	                
	                WebElement button = popup.findElement(
	                    By.xpath(".//button[contains(@class, 'btn-dark-ot')]"));
	                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
	                return;
	            } catch (Exception e2) {
	                try {
	                    Actions actions = new Actions(driver);
	                    actions.moveByOffset(400, 500).click().perform(); 
	                    System.out.println("Clicked using coordinates as fallback");
	                    return;
	                } catch (Exception e3) {
	                    throw new Exception("All approaches failed to click delete button: " + 
	                        e1.getMessage() + " | " + e2.getMessage() + " | " + e3.getMessage());
	                }
	            }
	        }
	    }

	    private void verifyResumeDeletion() throws Exception {
	        try {
	            new WebDriverWait(driver, Duration.ofSeconds(20))
	                .until(ExpectedConditions.or(
	                    ExpectedConditions.visibilityOfElementLocated(
	                        By.xpath("//*[contains(., 'deleted successfully') or contains(., 'Resume removed')]")),
	                    ExpectedConditions.invisibilityOfElementLocated(
	                        By.xpath("//*[contains(., 'Uploaded Resume')]")),
	                    ExpectedConditions.invisibilityOfElementLocated(
	                        By.xpath("//i[@data-title='delete-resume']"))
	                ));
	        } catch (TimeoutException e) {
	            throw new Exception("Failed to verify resume deletion: " + e.getMessage());
	        }
	    }
	    
	    private void uploadResume() throws Exception {
	        try {
	        	
	        	   String absolutePath = getAbsoluteResumePath();
	               System.out.println("Attempting to upload resume from: " + absolutePath);
	               
	        	  WebElement fileInput = new WebDriverWait(driver, Duration.ofSeconds(5))
	        	            .until(ExpectedConditions.presenceOfElementLocated(
	        	                By.xpath("//input[@type='file' and @id='attachCV']")));

	        	        fileInput.sendKeys(absolutePath);
	        	        System.out.println("Resume file path entered: " + absolutePath);
	        	        
	        	        waitForUploadCompletion();
	        	        
	        	        verifyResumeUpload();
	            
	            System.out.println("Resume uploaded successfully");
	        } catch (Exception e) {
	            System.out.println("Failed to upload resume: " + e.getMessage());
	            throw e;
	        }
	    }
	    
	    private String getAbsoluteResumePath() throws Exception {
	    	System.out.println("resumePath : "+resumePath);
	        String normalizedPath = resumePath.replace('/', File.separatorChar)
	                                                 .replace('\\', File.separatorChar);
	        
	        File file = new File(normalizedPath);
	        	        
	        if (!file.exists()) {
	        	 System.out.println("Resume file not found at: " + file.getAbsolutePath());
	        }
	        
	        return file.getAbsolutePath();
	    }
	    
	    private void waitForUploadCompletion() throws Exception {
	        try {
	            new WebDriverWait(driver, Duration.ofSeconds(30))
	                .until(ExpectedConditions.or(
	                    ExpectedConditions.visibilityOfElementLocated(
	                        By.xpath("//*[contains(text(),'Upload successful') or contains(text(),'Resume uploaded')]")),
	                    ExpectedConditions.invisibilityOfElementLocated(
	                        By.xpath("//*[contains(@class,'progress') or contains(@class,'loading')]")),
	                    ExpectedConditions.presenceOfElementLocated(
	                        By.xpath("//div[@id='result' and not(contains(text(),'0%'))]"))
	                ));
	        } catch (TimeoutException e) {
	            throw new Exception("Timeout waiting for upload to complete");
	        }
	    }

	    private void verifyResumeUpload() throws Exception {
	        try {
	            boolean isUploaded = new WebDriverWait(driver, Duration.ofSeconds(15))
	                .until(ExpectedConditions.or(
	                    ExpectedConditions.visibilityOfElementLocated(
	                        By.xpath("//*[contains(text(),'Uploaded Resume')]")),
	                    ExpectedConditions.visibilityOfElementLocated(
	                        By.xpath("//i[@data-title='delete-resume']")),
	                    ExpectedConditions.textToBePresentInElementLocated(
	                        By.id("result"), "100%")
	                ));
	            
	            if (!isUploaded) {
	                throw new Exception("No success indicators found after upload");
	            }
	        } catch (TimeoutException e) {
	            throw new Exception("Failed to verify resume upload: " + e.getMessage());
	        }
	    }
}
