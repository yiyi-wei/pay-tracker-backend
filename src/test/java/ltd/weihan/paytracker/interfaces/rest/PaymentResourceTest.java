package ltd.weihan.paytracker.interfaces.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
public class PaymentResourceTest {
    
    @Test
    public void testCreatePayment() {
        Map<String, String> payment = new HashMap<>();
        payment.put("amount", "100.50");
        payment.put("paymentMethod", "ALIPAY");
        payment.put("category", "FOOD");
        payment.put("description", "Lunch");
        payment.put("paymentDate", LocalDateTime.now().toString());
        
        given()
                .contentType(ContentType.JSON)
                .body(payment)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(201)
                .body("amount", is("100.50"))
                .body("paymentMethod", is("ALIPAY"))
                .body("category", is("FOOD"))
                .body("description", is("Lunch"));
    }
    
    @Test
    public void testGetPayment() {
        given()
                .when()
                .get("/api/payments/{id}", 1)
                .then()
                .statusCode(200)
                .body("id", is(1));
    }
    
    @Test
    public void testGetNonExistentPayment() {
        given()
                .when()
                .get("/api/payments/{id}", 999)
                .then()
                .statusCode(404);
    }
    
    @Test
    public void testUpdatePayment() {
        Map<String, String> updates = new HashMap<>();
        updates.put("amount", "200.00");
        updates.put("description", "Updated description");
        
        given()
                .contentType(ContentType.JSON)
                .body(updates)
                .when()
                .put("/api/payments/{id}", 1)
                .then()
                .statusCode(200)
                .body("amount", is("200.00"))
                .body("description", is("Updated description"));
    }
    
    @Test
    public void testDeletePayment() {
        given()
                .when()
                .delete("/api/payments/{id}", 1)
                .then()
                .statusCode(204);
        
        given()
                .when()
                .get("/api/payments/{id}", 1)
                .then()
                .statusCode(404);
    }
    
    @Test
    public void testGetPaymentsByCategory() {
        given()
                .when()
                .get("/api/payments/category/{category}", "FOOD")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }
    
    @Test
    public void testGetPaymentsByDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        given()
                .queryParam("startDate", startDate.toString())
                .queryParam("endDate", endDate.toString())
                .when()
                .get("/api/payments/date-range")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }
} 