package com.udacity.vehicles.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.client.prices.PriceClientRT;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;



/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    RestClientForTest restClientForTest;

    @Autowired
    private MockMvc mvc;
  //  @Autowired
  //  private WebApplicationContext webApplicationContext;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private PriceClientRT priceClientRT;

    @MockBean
    private MapsClient mapsClient;

    @MockBean
    private  CarRepository repository;



    private final Logger logger= LoggerFactory.getLogger(CarControllerTest.class);

   /** @Before
    public void init(){
        logger.debug("====set up MVC====");
        mvc= webAppContextSetup(webApplicationContext).build();
    }
   */
    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
   // @Order(1)
    public void createCar() throws Exception {
        logger.debug("====Test 1=====");
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(status().isCreated());
   }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    @Order(2)
    public void listCars() throws Exception {
        /**
         * TODO: Add a test to check that the `get` method works by calling
         *   the whole list of vehicles. This should utilize the car from `getCar()`
         *   below (the vehicle will be the first in the list).
         */
        logger.debug("====Test 2=====");
        mvc.perform(
                get(new URI("/cars"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    @Order(3)
    public void findCar() throws Exception {
        /**
         * TODO: Add a test to check that the `get` method works by calling
         *   a vehicle by ID. This should utilize the car from `getCar()` below.
         */

        logger.debug("====Test 3=====");
        mvc.perform(get("/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.details.model").value("Impala"));
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        /**
         * TODO: Add a test to check whether a vehicle is appropriately deleted
         *   when the `delete` method is called from the Car Controller. This
         *   should utilize the car from `getCar()` below.
         */
        logger.debug("===Test 4====");
        //delete it, but all are mocking, nothing in DB, expect notFound
        mvc.perform(delete("cars/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testWithRestTemplate() throws Exception{
        //test car creation
        logger.debug("==Test with RestTemplate:Create===");
        Car car=getCar();
        String jsonInString=restClientForTest.carToJsonString(car);
        String created_jsonInString = restClientForTest.create(port,"/cars/",jsonInString);
        Car created_car = restClientForTest.JsonStringToCar(created_jsonInString);
        logger.debug("created_car: id="+created_car.getId()+" created_car:model="+created_car.getDetails().getModel());
        Assertions.assertEquals(1,created_car.getId());
        Assertions.assertEquals("Impala",created_car.getDetails().getModel());

        //test car update.todo: looks the update is not working, need to find out
        created_car.setCondition(Condition.NEW);
        jsonInString=restClientForTest.carToJsonString(car);
        String updated_jsonInString=restClientForTest.update(port,"/cars/"+created_car.getId().toString(),jsonInString).getBody();
        logger.debug("updated Car="+updated_jsonInString);

        //test car get
        String queried_jsonInString=restClientForTest.get(port,"/cars/"+created_car.getId().toString());
        Car queried_car=restClientForTest.JsonStringToCar(queried_jsonInString);
        logger.debug("queried Car: condition="+queried_car.getCondition()+" model="+queried_car.getDetails().getModel());
        Assertions.assertEquals("Impala",queried_car.getDetails().getModel());

        //test car delete
        ResponseEntity<String> res=restClientForTest.delete(port,"/cars/"+created_car.getId().toString());
        Assertions.assertEquals(HttpStatus.OK,res.getStatusCode());

    }


    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}