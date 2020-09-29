import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import api.ForecastServiceImpl;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyData;
import io.restassured.specification.RequestSpecification;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class AppTest extends Assertions {

    public static final WireMockClassRule forecastServiceServer = new WireMockClassRule(7010);

    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl("https://localhost:7010")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static ForecastServiceImpl forecastService;

    @Before
    public void setUp() throws IOException {
        forecastService = new ForecastServiceImpl(RETROFIT);
        forecastServiceServer.start();
        WireMock.configureFor("localhost", forecastServiceServer.port());
    }

    @After
    public void tearDown() {
        forecastServiceServer.resetAll();
    }


    /*
    * "javax.net.ssl.SSLException: Unsupported or unrecognized SSL message"
    * */
//    @Test
//    public void getForecastTest() throws Exception{
//        stubFor(get(urlPathEqualTo("/api/location/search"))
//                        .withQueryParam("query", equalTo("dubai"))
//                        .willReturn(aResponse().withStatus(200)
//                                               .withBodyFile("city_query_dubai.json"))
//        );
//
//        stubFor(get(urlPathEqualTo("/api/location/1940345/2020/09/26/"))
//                        .willReturn(aResponse().withStatus(200)
//                                               .withBodyFile("forecast_dubai_26_sept.json"))
//        );
//
//        LocalDate tomorrow = LocalDate.now().plusDays(1);
//        String expected = forecastService.getForecast("1940345", tomorrow);
//        assertEquals("w", expected);
//    }

    @Test
    public void appTestWithSearchQuery() {

        stubFor(get(urlPathEqualTo("/api/location/search"))
                        .withQueryParam("query", equalTo("dubai"))
                        .willReturn(aResponse().withStatus(200)
                                               .withBodyFile("city_query_dubai.json"))
        );

        stubFor(get(urlPathEqualTo("/api/location/1940345/2020/09/26/"))
                        .willReturn(aResponse().withStatus(200)
                                               .withBodyFile("forecast_dubai_26_sept.json"))
        );

        RestAssured.baseURI = "http://localhost:7010/api/location/search";
        RequestSpecification httpRequest = RestAssured.given().param("query", "dubai");
        Response res = httpRequest.get();
        ResponseBodyData body = res.getBody();
        String bodyAsString = body.asString();
        Assert.assertEquals(res.getStatusCode(), 200);
        Assert.assertTrue(bodyAsString.contains("Dubai"));
    }

    /*
    * Contract Test
    * */
    @Test
    public void appTestWithCityIDAndDate() throws Exception {

            stubFor(get(urlPathEqualTo("/api/location/search"))
                    .withQueryParam("query", equalTo("dubai"))
                    .willReturn(aResponse().withStatus(200)
                            .withBodyFile("city_query_dubai.json"))
            );

            stubFor(get(urlPathEqualTo("/api/location/1940345/2020/09/26/"))
                    .willReturn(aResponse().withStatus(200)
                            .withBodyFile("forecast_dubai_26_sept.json"))
            );


            String cityId = "1940345/";
            String date = "2020/09/26/";
            JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder()
                .setValidationConfiguration(
                        ValidationConfiguration.newBuilder()
                                .setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
                .freeze();
            RestAssured.given()
                .when()
                .get(new URI("http://localhost:7010/api/location/"+cityId+date))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("forecast_schema.json").using(jsonSchemaFactory));
    }
}
